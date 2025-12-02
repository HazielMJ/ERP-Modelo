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
    
    public Ruta crearRuta(Ruta ruta) {
        if (ruta.getCodigoRuta() != null && rutaRepository.findByCodigoRuta(ruta.getCodigoRuta()).isPresent()) {
            throw new RuntimeException("Ya existe una ruta con ese código");
        }
        return rutaRepository.save(ruta);
    }
    
    public Ruta actualizarRuta(Integer id, Ruta ruta) {
        Ruta existente = obtenerRutaPorId(id);
        existente.setNombreRuta(ruta.getNombreRuta());
        existente.setOrigen(ruta.getOrigen());
        existente.setDestino(ruta.getDestino());
        existente.setDistanciaKm(ruta.getDistanciaKm());
        existente.setTiempoEstimadoMin(ruta.getTiempoEstimadoMin());
        existente.setCostoEstimado(ruta.getCostoEstimado());
        return rutaRepository.save(existente);
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
