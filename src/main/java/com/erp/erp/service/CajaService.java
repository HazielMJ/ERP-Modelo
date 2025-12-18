package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.exception.ResourceNotFoundException;
import com.erp.erp.exception.BusinessException;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CajaService {
    
    private final CajaRepository cajaRepository;
    private final AperturaCajaRepository aperturaCajaRepository;

    public Caja crearCaja(Caja caja) {
        log.info("Creando nueva caja: {}", caja.getNumeroCaja());
        

        if (cajaRepository.existsByNumeroCaja(caja.getNumeroCaja())) {
            throw new BusinessException("Ya existe una caja con el número: " + caja.getNumeroCaja());
        }
        
        if (cajaRepository.existsByNombreCaja(caja.getNombreCaja())) {
            throw new BusinessException("Ya existe una caja con el nombre: " + caja.getNombreCaja());
        }
        
        Caja cajaNueva = cajaRepository.save(caja);
        log.info("Caja creada exitosamente: {}", cajaNueva.getIdCaja());
        return cajaNueva;
    }
    
    public Caja actualizarCaja(Integer id, Caja caja) {
        log.info("Actualizando caja: {}", id);
        
        Caja cajaExistente = obtenerCajaPorId(id);
        
        if (!cajaExistente.getNumeroCaja().equals(caja.getNumeroCaja())) {
            if (cajaRepository.existsByNumeroCaja(caja.getNumeroCaja())) {
                throw new BusinessException("Ya existe una caja con ese número");
            }
        }
        
        cajaExistente.setNumeroCaja(caja.getNumeroCaja());
        cajaExistente.setNombreCaja(caja.getNombreCaja());
        cajaExistente.setUbicacion(caja.getUbicacion());
        cajaExistente.setEstado(caja.getEstado());
        
        return cajaRepository.save(cajaExistente);
    }
    

    public void eliminarCaja(Integer id) {
        log.info("Eliminando caja: {}", id);
        
        Caja caja = obtenerCajaPorId(id);
        
        List<AperturaCaja> aperturas = aperturaCajaRepository.findByCaja(caja);
        if (!aperturas.isEmpty()) {
            throw new BusinessException("No se puede eliminar la caja porque tiene aperturas registradas");
        }
        
        cajaRepository.delete(caja);
        log.info("Caja eliminada exitosamente");
    }
    

    public Caja cambiarEstadoCaja(Integer id, Caja.EstadoCaja nuevoEstado) {
        log.info("Cambiando estado de caja {} a {}", id, nuevoEstado);
        
        Caja caja = obtenerCajaPorId(id);
        
        if (nuevoEstado != Caja.EstadoCaja.ACTIVA) {
            if (aperturaCajaRepository.existeAperturaAbierta(id)) {
                throw new BusinessException("No se puede cambiar el estado de la caja porque tiene una apertura abierta");
            }
        }
        
        caja.setEstado(nuevoEstado);
        return cajaRepository.save(caja);
    }
    

    public Caja activarCaja(Integer id) {
        return cambiarEstadoCaja(id, Caja.EstadoCaja.ACTIVA);
    }
    
    
    public Caja desactivarCaja(Integer id) {
        return cambiarEstadoCaja(id, Caja.EstadoCaja.INACTIVA);
    }
    

    public Caja ponerCajaEnMantenimiento(Integer id) {
        return cambiarEstadoCaja(id, Caja.EstadoCaja.MANTENIMIENTO);
    }
    
    
    @Transactional(readOnly = true)
    public Caja obtenerCajaPorId(Integer id) {
        return cajaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada con id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Optional<Caja> obtenerCajaPorNumero(String numeroCaja) {
        return cajaRepository.findByNumeroCaja(numeroCaja);
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerTodasLasCajas() {
        return cajaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerCajasPorEstado(Caja.EstadoCaja estado) {
        return cajaRepository.findByEstado(estado);
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerCajasActivas() {
        return cajaRepository.findCajasActivas();
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerCajasDisponibles() {
        return cajaRepository.findCajasDisponibles();
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerCajasConAperturaAbierta() {
        return cajaRepository.findCajasConAperturaAbierta();
    }
    
    @Transactional(readOnly = true)
    public List<Caja> obtenerCajasSinAperturaHoy() {
        return cajaRepository.findCajasSinAperturaHoy();
    }
    
    @Transactional(readOnly = true)
    public boolean existeCajaPorNumero(String numeroCaja) {
        return cajaRepository.existsByNumeroCaja(numeroCaja);
    }
    
    @Transactional(readOnly = true)
    public Long contarCajasPorEstado(Caja.EstadoCaja estado) {
        return cajaRepository.countByEstado(estado);
    }
}
