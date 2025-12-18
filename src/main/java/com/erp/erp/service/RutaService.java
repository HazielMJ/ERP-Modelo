package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RutaService {
    private final RutaRepository rutaRepository;
    

    @Transactional(readOnly = true)
    public List<Ruta> obtenerTodas() {
        return rutaRepository.findAll();
    }
    

    public Ruta crearRuta(Ruta ruta) {
        if (ruta.getCodigoRuta() != null && rutaRepository.findByCodigoRuta(ruta.getCodigoRuta()).isPresent()) {
            throw new RuntimeException("Ya existe una ruta con ese código");
        }
        return rutaRepository.save(ruta);
    }
    

    public Ruta actualizarRuta(Integer id, Ruta ruta) {
        Ruta existente = obtenerRutaPorId(id);
        
        if (ruta.getCodigoRuta() != null) {
            existente.setCodigoRuta(ruta.getCodigoRuta());
        }
        if (ruta.getNombreRuta() != null) {
            existente.setNombreRuta(ruta.getNombreRuta());
        }
        if (ruta.getOrigen() != null) {
            existente.setOrigen(ruta.getOrigen());
        }
        if (ruta.getDestino() != null) {
            existente.setDestino(ruta.getDestino());
        }
        if (ruta.getDistanciaKm() != null) {
            existente.setDistanciaKm(ruta.getDistanciaKm());
        }
        if (ruta.getTiempoEstimadoMin() != null) {
            existente.setTiempoEstimadoMin(ruta.getTiempoEstimadoMin());
        }
        if (ruta.getCostoEstimado() != null) {
            existente.setCostoEstimado(ruta.getCostoEstimado());
        }
        if (ruta.getReferencias() != null) {
            existente.setReferencias(ruta.getReferencias());
        }
        if (ruta.getEstado() != null) {
            existente.setEstado(ruta.getEstado());
        }
        
        return rutaRepository.save(existente);
    }
    

    public void eliminarRuta(Integer id) {
        Ruta ruta = obtenerRutaPorId(id);
        ruta.setEstado(Ruta.EstadoRuta.INACTIVA);
        rutaRepository.save(ruta);
    }
    
    @Transactional(readOnly = true)
    public Ruta obtenerRutaPorId(Integer id) {
        return rutaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
    }
    

    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasActivas() {
        return rutaRepository.findByEstado(Ruta.EstadoRuta.ACTIVA);
    }
}
