package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NominaService {
    private final NominaRepository nominaRepository;
    private final DetalleNominaRepository detalleNominaRepository;
    private final EmpleadoRepository empleadoRepository;
    
    public Nomina crearNomina(Nomina nomina) {
        calcularTotales(nomina);
        return nominaRepository.save(nomina);
    }
    
    public Nomina agregarPercepcion(Integer nominaId, String concepto, BigDecimal monto, String claveSat) {
        Nomina nomina = obtenerNominaPorId(nominaId);
        
        DetalleNomina detalle = DetalleNomina.builder()
            .nomina(nomina)
            .tipo(DetalleNomina.TipoConcepto.PERCEPCION)
            .concepto(concepto)
            .claveSat(claveSat)
            .monto(monto)
            .build();
        
        detalleNominaRepository.save(detalle);
        nomina.getDetalles().add(detalle);
        calcularTotales(nomina);
        return nominaRepository.save(nomina);
    }
    
    public Nomina agregarDeduccion(Integer nominaId, String concepto, BigDecimal monto, String claveSat) {
        Nomina nomina = obtenerNominaPorId(nominaId);
        
        DetalleNomina detalle = DetalleNomina.builder()
            .nomina(nomina)
            .tipo(DetalleNomina.TipoConcepto.DEDUCCION)
            .concepto(concepto)
            .claveSat(claveSat)
            .monto(monto)
            .build();
        
        detalleNominaRepository.save(detalle);
        nomina.getDetalles().add(detalle);
        calcularTotales(nomina);
        return nominaRepository.save(nomina);
    }
    
    public void procesarPagoNomina(Integer id) {
        Nomina nomina = obtenerNominaPorId(id);
        if (nomina.getEstado() != Nomina.EstadoNomina.PENDIENTE) {
            throw new RuntimeException("La nómina ya fue procesada");
        }
        nomina.setEstado(Nomina.EstadoNomina.PAGADA);
        nominaRepository.save(nomina);
    }
    
    public Nomina generarNominaAutomatica(Integer empleadoId, LocalDate inicio, LocalDate fin) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        // Calcular días trabajados
        int diasTrabajados = (int) java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1;
        BigDecimal salarioDiario = empleado.getSalarioBase().divide(new BigDecimal("30"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal salarioBase = salarioDiario.multiply(new BigDecimal(diasTrabajados));
        
        Nomina nomina = Nomina.builder()
            .empleado(empleado)
            .periodoNomina(inicio.toString() + " a " + fin.toString())
            .fechaInicio(inicio)
            .fechaFin(fin)
            .diasTrabajados(diasTrabajados)
            .salarioDiario(salarioDiario)
            .tipoNomina(Nomina.TipoNomina.QUINCENAL)
            .estado(Nomina.EstadoNomina.PENDIENTE)
            .build();
        
        nomina = nominaRepository.save(nomina);
        
        // Agregar percepción de salario base
        agregarPercepcion(nomina.getIdNomina(), "Salario Base", salarioBase, "001");
        
        // Calcular y agregar deducciones (ejemplo: ISR e IMSS)
        BigDecimal isr = calcularISR(salarioBase);
        if (isr.compareTo(BigDecimal.ZERO) > 0) {
            agregarDeduccion(nomina.getIdNomina(), "ISR", isr, "002");
        }
        
        BigDecimal imss = salarioBase.multiply(new BigDecimal("0.0375")); // 3.75% ejemplo
        agregarDeduccion(nomina.getIdNomina(), "IMSS", imss, "001");
        
        return nominaRepository.findById(nomina.getIdNomina()).orElseThrow();
    }
    
    private void calcularTotales(Nomina nomina) {
        BigDecimal totalPercepciones = nomina.getDetalles().stream()
            .filter(d -> d.getTipo() == DetalleNomina.TipoConcepto.PERCEPCION)
            .map(DetalleNomina::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDeducciones = nomina.getDetalles().stream()
            .filter(d -> d.getTipo() == DetalleNomina.TipoConcepto.DEDUCCION)
            .map(DetalleNomina::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        nomina.setTotalPercepciones(totalPercepciones);
        nomina.setTotalDeducciones(totalDeducciones);
        nomina.setTotalNeto(totalPercepciones.subtract(totalDeducciones));
    }
    
    private BigDecimal calcularISR(BigDecimal ingreso) {
        // Cálculo simplificado de ISR - En producción usar tablas del SAT
        if (ingreso.compareTo(new BigDecimal("7000")) <= 0) {
            return BigDecimal.ZERO;
        } else if (ingreso.compareTo(new BigDecimal("15000")) <= 0) {
            return ingreso.multiply(new BigDecimal("0.10"));
        } else {
            return ingreso.multiply(new BigDecimal("0.20"));
        }
    }
    
    @Transactional(readOnly = true)
    public Nomina obtenerNominaPorId(Integer id) {
        return nominaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nómina no encontrada"));
    }
    
    @Transactional(readOnly = true)
    public List<Nomina> obtenerNominasPorEmpleado(Integer empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId).orElseThrow();
        return nominaRepository.findByEmpleado(empleado);
    }
}
