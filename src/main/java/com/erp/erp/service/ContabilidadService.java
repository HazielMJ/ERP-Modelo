package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ContabilidadService {
    private final ContabilidadRepository contabilidadRepository;
    private final DetalleContableRepository detalleContableRepository;
    
    public Contabilidad crearAsientoContable(Contabilidad asiento) {
        validarAsiento(asiento);
        return contabilidadRepository.save(asiento);
    }
    
    public Contabilidad actualizarAsientoContable(Integer id, Contabilidad asiento) {
        Contabilidad existente = obtenerAsientoPorId(id);
        if (existente.getEstado() == Contabilidad.EstadoAsiento.CONTABILIZADO) {
            throw new RuntimeException("No se puede modificar un asiento contabilizado");
        }
        existente.setDescripcion(asiento.getDescripcion());
        existente.setFechaAsiento(asiento.getFechaAsiento());
        validarAsiento(existente);
        return contabilidadRepository.save(existente);
    }
    
    public void contabilizarAsiento(Integer id) {
        Contabilidad asiento = obtenerAsientoPorId(id);
        if (asiento.getEstado() != Contabilidad.EstadoAsiento.BORRADOR) {
            throw new RuntimeException("Solo se pueden contabilizar asientos en borrador");
        }
        validarAsiento(asiento);
        asiento.setEstado(Contabilidad.EstadoAsiento.CONTABILIZADO);
        asiento.setFechaContabilizacion(LocalDateTime.now());
        contabilidadRepository.save(asiento);
    }
    
    public void anularAsiento(Integer id) {
        Contabilidad asiento = obtenerAsientoPorId(id);
        asiento.setEstado(Contabilidad.EstadoAsiento.ANULADO);
        contabilidadRepository.save(asiento);
    }
    
    public DetalleContable agregarLineaAsiento(Integer asientoId, DetalleContable detalle) {
        Contabilidad asiento = obtenerAsientoPorId(asientoId);
        if (asiento.getEstado() == Contabilidad.EstadoAsiento.CONTABILIZADO) {
            throw new RuntimeException("No se pueden agregar líneas a un asiento contabilizado");
        }
        detalle.setAsiento(asiento);
        DetalleContable guardado = detalleContableRepository.save(detalle);
        recalcularTotales(asiento);
        return guardado;
    }
    
    private void validarAsiento(Contabilidad asiento) {
        BigDecimal diferencia = asiento.getTotalDebe().subtract(asiento.getTotalHaber()).abs();
        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("El asiento contable no está cuadrado. Diferencia: " + diferencia);
        }
    }
    
    private void recalcularTotales(Contabilidad asiento) {
        List<DetalleContable> detalles = detalleContableRepository.findByAsiento(asiento);
        BigDecimal totalDebe = detalles.stream()
            .map(DetalleContable::getDebe)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = detalles.stream()
            .map(DetalleContable::getHaber)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        asiento.setTotalDebe(totalDebe);
        asiento.setTotalHaber(totalHaber);
        contabilidadRepository.save(asiento);
    }
    
    @Transactional(readOnly = true)
    public Contabilidad obtenerAsientoPorId(Integer id) {
        return contabilidadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asiento contable no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Contabilidad> obtenerAsientosPorPeriodo(String periodo) {
        return contabilidadRepository.findByPeriodoContable(periodo);
    }
    
    @Transactional(readOnly = true)
    public List<Contabilidad> obtenerAsientosPorFecha(LocalDate inicio, LocalDate fin) {
        return contabilidadRepository.findByFechaAsientoBetween(inicio, fin);
    }
}
