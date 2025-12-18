package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.exception.ResourceNotFoundException;
import com.erp.erp.exception.BusinessException;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AperturaCajaService {
    
    private final AperturaCajaRepository aperturaCajaRepository;
    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    
    public AperturaCaja abrirCaja(Integer cajaId, Integer usuarioId, BigDecimal saldoInicial, String observaciones) {
        log.info("Abriendo caja {} por usuario {}", cajaId, usuarioId);
        
        Caja caja = cajaRepository.findById(cajaId)
            .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada"));
        
        if (!caja.isActiva()) {
            throw new BusinessException("La caja no está activa. Estado actual: " + caja.getEstado());
        }
        
        if (aperturaCajaRepository.existeAperturaAbierta(cajaId)) {
            throw new BusinessException("La caja ya está abierta");
        }
        
        if (aperturaCajaRepository.existeAperturaAbiertaParaUsuario(usuarioId)) {
            throw new BusinessException("El usuario ya tiene una caja abierta");
        }

        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El saldo inicial no puede ser negativo");
        }
        

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        

        AperturaCaja apertura = AperturaCaja.crearApertura(caja, usuario, saldoInicial, observaciones);
        
        AperturaCaja aperturaGuardada = aperturaCajaRepository.save(apertura);
        log.info("Caja abierta exitosamente. Apertura ID: {}", aperturaGuardada.getIdApertura());
        
        return aperturaGuardada;
    }
    
    public AperturaCaja abrirCaja(AperturaCaja aperturaCaja) {
        log.info("Abriendo caja con objeto completo");
        

        AperturaCaja nuevaApertura = AperturaCaja.crearApertura(
            aperturaCaja.getCaja(),
            aperturaCaja.getUsuario(), 
            aperturaCaja.getSaldoInicial(),
            aperturaCaja.getObservaciones()
        );
        

        if (aperturaCajaRepository.existeAperturaAbierta(nuevaApertura.getCaja().getIdCaja())) {
            throw new BusinessException("La caja ya está abierta");
        }
        
        if (aperturaCajaRepository.existeAperturaAbiertaParaUsuario(nuevaApertura.getUsuario().getId())) {
            throw new BusinessException("El usuario ya tiene una caja abierta");
        }
        
        AperturaCaja aperturaGuardada = aperturaCajaRepository.save(nuevaApertura);
        log.info("Caja abierta exitosamente. Apertura ID: {}", aperturaGuardada.getIdApertura());
        
        return aperturaGuardada;
    }
    
    public AperturaCaja cerrarCaja(Integer aperturaId, BigDecimal saldoReal, String observaciones) {
        log.info("Cerrando apertura de caja: {}", aperturaId);
        
        AperturaCaja apertura = obtenerAperturaPorId(aperturaId);
        
        if (!apertura.isAbierta()) {
            throw new BusinessException("La caja ya está cerrada");
        }
        
        if (saldoReal == null || saldoReal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El saldo real es inválido");
        }
        
        BigDecimal totalIngresos = movimientoCajaRepository
            .getTotalIngresosByApertura(aperturaId);
        BigDecimal totalEgresos = movimientoCajaRepository
            .getTotalEgresosByApertura(aperturaId);
        
        apertura.setTotalIngresos(totalIngresos != null ? totalIngresos : BigDecimal.ZERO);
        apertura.setTotalEgresos(totalEgresos != null ? totalEgresos : BigDecimal.ZERO);
        
    
        apertura.cerrar(saldoReal, observaciones);
        
        AperturaCaja aperturaCerrada = aperturaCajaRepository.save(apertura);
        
        
        if (aperturaCerrada.tieneDiferencia()) {
            log.warn("Caja cerrada con diferencia de ${}: {}", 
                aperturaCerrada.getDiferencia(), 
                aperturaCerrada.tieneFaltante() ? "FALTANTE" : "SOBRANTE");
        } else {
            log.info("Caja cerrada sin diferencias");
        }
        
        return aperturaCerrada;
    }
    
    public void actualizarTotalesApertura(Integer aperturaId) {
        log.debug("Actualizando totales de apertura: {}", aperturaId);
        
        AperturaCaja apertura = obtenerAperturaPorId(aperturaId);
        
        BigDecimal totalIngresos = movimientoCajaRepository
            .getTotalIngresosByApertura(aperturaId);
        BigDecimal totalEgresos = movimientoCajaRepository
            .getTotalEgresosByApertura(aperturaId);
        
        apertura.setTotalIngresos(totalIngresos != null ? totalIngresos : BigDecimal.ZERO);
        apertura.setTotalEgresos(totalEgresos != null ? totalEgresos : BigDecimal.ZERO);
        
        aperturaCajaRepository.save(apertura);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal obtenerSaldoActual(Integer aperturaId) {
        AperturaCaja apertura = obtenerAperturaPorId(aperturaId);
        
        if (apertura.isCerrada()) {
            return apertura.getSaldoFinal();
        }
        
        BigDecimal ingresos = movimientoCajaRepository
            .getTotalIngresosByApertura(aperturaId);
        BigDecimal egresos = movimientoCajaRepository
            .getTotalEgresosByApertura(aperturaId);
        
        ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        egresos = egresos != null ? egresos : BigDecimal.ZERO;
        
        return apertura.getSaldoInicial().add(ingresos).subtract(egresos);
    }
    
    @Transactional(readOnly = true)
    public ResumenAperturaDTO obtenerResumenApertura(Integer aperturaId) {
        AperturaCaja apertura = obtenerAperturaPorId(aperturaId);
        
        BigDecimal totalIngresos = movimientoCajaRepository
            .getTotalIngresosByApertura(aperturaId);
        BigDecimal totalEgresos = movimientoCajaRepository
            .getTotalEgresosByApertura(aperturaId);
        BigDecimal saldoActual = obtenerSaldoActual(aperturaId);
        
        Long numeroIngresos = movimientoCajaRepository
            .countByAperturaAndTipo(aperturaId, MovimientoCaja.TipoMovimiento.INGRESO);
        Long numeroEgresos = movimientoCajaRepository
            .countByAperturaAndTipo(aperturaId, MovimientoCaja.TipoMovimiento.EGRESO);
        

        List<Object[]> totalesPorFormaPago = movimientoCajaRepository
            .getTotalPorFormaPago(aperturaId);
        

        List<Object[]> estadisticasPorCategoria = movimientoCajaRepository
            .getEstadisticasPorCategoria(aperturaId);
        
        return new ResumenAperturaDTO(
            apertura,
            totalIngresos != null ? totalIngresos : BigDecimal.ZERO,
            totalEgresos != null ? totalEgresos : BigDecimal.ZERO,
            saldoActual,
            numeroIngresos,
            numeroEgresos,
            totalesPorFormaPago,
            estadisticasPorCategoria
        );
    }
    
    
    @Transactional(readOnly = true)
    public AperturaCaja obtenerAperturaPorId(Integer id) {
        return aperturaCajaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada con id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerTodasLasAperturas() {
        return aperturaCajaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<AperturaCaja> obtenerCajaAbierta(Integer cajaId) {
        return aperturaCajaRepository.findCajaAbierta(cajaId);
    }
    
    @Transactional(readOnly = true)
    public Optional<AperturaCaja> obtenerAperturaAbiertaPorUsuario(Integer usuarioId) {
        return aperturaCajaRepository.findAperturaAbiertaPorUsuario(usuarioId);
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasAbiertas() {
        return aperturaCajaRepository.findAperturasAbiertas();
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasDelDia() {
        return aperturaCajaRepository.findAperturasDelDia();
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return aperturaCajaRepository.findByFechaAperturaBetween(inicio, fin);
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasConDiferencias() {
        return aperturaCajaRepository.findAperturasConDiferencias();
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasConFaltante() {
        return aperturaCajaRepository.findAperturasConFaltante();
    }
    
    @Transactional(readOnly = true)
    public List<AperturaCaja> obtenerAperturasConSobrante() {
        return aperturaCajaRepository.findAperturasConSobrante();
    }
    
    @Transactional(readOnly = true)
    public boolean existeAperturaAbierta(Integer cajaId) {
        return aperturaCajaRepository.existeAperturaAbierta(cajaId);
    }
    
    @Transactional(readOnly = true)
    public boolean usuarioTieneAperturaAbierta(Integer usuarioId) {
        return aperturaCajaRepository.existeAperturaAbiertaParaUsuario(usuarioId);
    }
    
    
    public record ResumenAperturaDTO(
        AperturaCaja apertura,
        BigDecimal totalIngresos,
        BigDecimal totalEgresos,
        BigDecimal saldoActual,
        Long numeroIngresos,
        Long numeroEgresos,
        List<Object[]> totalesPorFormaPago,
        List<Object[]> estadisticasPorCategoria
    ) {}
}
