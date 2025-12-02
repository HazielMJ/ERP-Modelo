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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MovimientoCajaService {
    
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final AperturaCajaRepository aperturaCajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentasRepository ventaRepository;
    private final AperturaCajaService aperturaCajaService;
    
    /**
     * Registrar movimiento genérico
     */
    public MovimientoCaja registrarMovimiento(Integer aperturaId, MovimientoCaja movimiento) {
        log.info("Registrando movimiento en apertura {}: {} - ${}", 
            aperturaId, movimiento.getConcepto(), movimiento.getMonto());
        
        AperturaCaja apertura = aperturaCajaRepository.findById(aperturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Apertura de caja no encontrada"));
        
        if (!apertura.isAbierta()) {
            throw new BusinessException("No se pueden registrar movimientos en una caja cerrada");
        }
        
        // Validar monto
        if (movimiento.getMonto() == null || movimiento.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a 0");
        }
        
        // Validar saldo para egresos
        if (movimiento.getTipoMovimiento() == MovimientoCaja.TipoMovimiento.EGRESO) {
            BigDecimal saldoActual = aperturaCajaService.obtenerSaldoActual(aperturaId);
            if (saldoActual.compareTo(movimiento.getMonto()) < 0) {
                throw new BusinessException(
                    String.format("Saldo insuficiente. Disponible: $%s, Requerido: $%s",
                        saldoActual, movimiento.getMonto())
                );
            }
        }
        
        // Establecer apertura y guardar
        movimiento.setApertura(apertura);
        MovimientoCaja movimientoGuardado = movimientoCajaRepository.save(movimiento);
        
        // Actualizar totales de la apertura
        aperturaCajaService.actualizarTotalesApertura(aperturaId);
        
        log.info("Movimiento registrado exitosamente. ID: {}", movimientoGuardado.getIdMovimiento());
        return movimientoGuardado;
    }
    
    /**
     * Registrar venta en caja
     */
    public MovimientoCaja registrarVenta(Integer aperturaId, Integer ventaId, 
                                        BigDecimal monto, MovimientoCaja.FormaPago formaPago,
                                        Integer usuarioId) {
        log.info("Registrando venta {} en apertura {}", ventaId, aperturaId);
        
        // Validar que la venta exista
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        
        // Validar que no se haya registrado ya
        if (movimientoCajaRepository.existeMovimientoParaVenta(ventaId)) {
            throw new BusinessException("Esta venta ya fue registrada en caja");
        }
        
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Crear movimiento de venta
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .tipoMovimiento(MovimientoCaja.TipoMovimiento.INGRESO)
            .categoria(MovimientoCaja.CategoriaMovimiento.VENTA)
            .monto(monto)
            .concepto("Venta " + venta.getNumeroVenta())
            .usuario(usuario)
            .venta(venta)
            .formaPago(formaPago)
            .referencia(venta.getNumeroVenta())
            .build();
        
        return registrarMovimiento(aperturaId, movimiento);
    }
    
    /**
     * Registrar retiro de efectivo
     */
    public MovimientoCaja registrarRetiro(Integer aperturaId, BigDecimal monto, 
                                         String concepto, Integer usuarioId, String referencia) {
        log.info("Registrando retiro de ${} en apertura {}", monto, aperturaId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .tipoMovimiento(MovimientoCaja.TipoMovimiento.EGRESO)
            .categoria(MovimientoCaja.CategoriaMovimiento.RETIRO)
            .monto(monto)
            .concepto(concepto)
            .usuario(usuario)
            .formaPago(MovimientoCaja.FormaPago.EFECTIVO)
            .referencia(referencia)
            .build();
        
        return registrarMovimiento(aperturaId, movimiento);
    }
    
    /**
     * Registrar gasto
     */
    public MovimientoCaja registrarGasto(Integer aperturaId, BigDecimal monto, 
                                        String concepto, Integer usuarioId, String referencia) {
        log.info("Registrando gasto de ${} en apertura {}", monto, aperturaId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .tipoMovimiento(MovimientoCaja.TipoMovimiento.EGRESO)
            .categoria(MovimientoCaja.CategoriaMovimiento.GASTO)
            .monto(monto)
            .concepto(concepto)
            .usuario(usuario)
            .formaPago(MovimientoCaja.FormaPago.EFECTIVO)
            .referencia(referencia)
            .build();
        
        return registrarMovimiento(aperturaId, movimiento);
    }
    
    /**
     * Registrar depósito
     */
    public MovimientoCaja registrarDeposito(Integer aperturaId, BigDecimal monto, 
                                           String concepto, Integer usuarioId, String referencia) {
        log.info("Registrando depósito de ${} en apertura {}", monto, aperturaId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .tipoMovimiento(MovimientoCaja.TipoMovimiento.INGRESO)
            .categoria(MovimientoCaja.CategoriaMovimiento.DEPOSITO)
            .monto(monto)
            .concepto(concepto)
            .usuario(usuario)
            .formaPago(MovimientoCaja.FormaPago.EFECTIVO)
            .referencia(referencia)
            .build();
        
        return registrarMovimiento(aperturaId, movimiento);
    }
    
    /**
     * Registrar ajuste (puede ser ingreso o egreso)
     */
    public MovimientoCaja registrarAjuste(Integer aperturaId, BigDecimal monto,
                                         MovimientoCaja.TipoMovimiento tipo,
                                         String concepto, Integer usuarioId, String referencia) {
        log.info("Registrando ajuste de ${} en apertura {}", monto, aperturaId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        MovimientoCaja movimiento = MovimientoCaja.builder()
            .tipoMovimiento(tipo)
            .categoria(MovimientoCaja.CategoriaMovimiento.AJUSTE)
            .monto(monto)
            .concepto(concepto)
            .usuario(usuario)
            .formaPago(MovimientoCaja.FormaPago.EFECTIVO)
            .referencia(referencia)
            .build();
        
        return registrarMovimiento(aperturaId, movimiento);
    }
    
    /**
     * Eliminar movimiento (solo si la caja está abierta)
     */
    public void eliminarMovimiento(Integer movimientoId) {
        log.info("Eliminando movimiento: {}", movimientoId);
        
        MovimientoCaja movimiento = obtenerMovimientoPorId(movimientoId);
        
        if (!movimiento.getApertura().isAbierta()) {
            throw new BusinessException("No se pueden eliminar movimientos de una caja cerrada");
        }
        
        // No se pueden eliminar movimientos de ventas
        if (movimiento.isVenta()) {
            throw new BusinessException("No se pueden eliminar movimientos de ventas");
        }
        
        Integer aperturaId = movimiento.getApertura().getIdApertura();
        movimientoCajaRepository.delete(movimiento);
        
        // Actualizar totales
        aperturaCajaService.actualizarTotalesApertura(aperturaId);
        
        log.info("Movimiento eliminado exitosamente");
    }
    
    // ==================== CONSULTAS ====================
    
    @Transactional(readOnly = true)
    public MovimientoCaja obtenerMovimientoPorId(Integer id) {
        return movimientoCajaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movimiento de caja no encontrado con id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosPorApertura(Integer aperturaId) {
        return movimientoCajaRepository.findByAperturaIdOrderByFecha(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosPorTipo(MovimientoCaja.TipoMovimiento tipo) {
        return movimientoCajaRepository.findByTipoMovimiento(tipo);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosPorCategoria(MovimientoCaja.CategoriaMovimiento categoria) {
        return movimientoCajaRepository.findByCategoria(categoria);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerVentasPorApertura(Integer aperturaId) {
        return movimientoCajaRepository.findVentasByApertura(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerRetirosPorApertura(Integer aperturaId) {
        return movimientoCajaRepository.findRetirosByApertura(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerGastosPorApertura(Integer aperturaId) {
        return movimientoCajaRepository.findGastosByApertura(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosDelDia() {
        return movimientoCajaRepository.findMovimientosDelDia();
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoCaja> obtenerMovimientosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoCajaRepository.findByFechaMovimientoBetween(inicio, fin);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalPorTipo(Integer aperturaId, MovimientoCaja.TipoMovimiento tipo) {
        BigDecimal total = movimientoCajaRepository.getTotalByAperturaAndTipo(aperturaId, tipo);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> obtenerTotalesPorFormaPago(Integer aperturaId) {
        return movimientoCajaRepository.getTotalPorFormaPago(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorCategoria(Integer aperturaId) {
        return movimientoCajaRepository.getEstadisticasPorCategoria(aperturaId);
    }
    
    @Transactional(readOnly = true)
    public boolean existeMovimientoParaVenta(Integer ventaId) {
        return movimientoCajaRepository.existeMovimientoParaVenta(ventaId);
    }
}