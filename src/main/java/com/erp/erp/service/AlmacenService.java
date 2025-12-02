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
public class AlmacenService {
    private final AlmacenRepository almacenRepository;
    
    public Almacen crearAlmacen(Almacen almacen) {
        if (almacenRepository.findByCodigoAlmacen(almacen.getCodigoAlmacen()).isPresent()) {
            throw new RuntimeException("Ya existe un almacén con ese código");
        }
        return almacenRepository.save(almacen);
    }
    
    public Almacen actualizarAlmacen(Integer id, Almacen almacen) {
        Almacen existente = obtenerAlmacenPorId(id);
        existente.setNombreAlmacen(almacen.getNombreAlmacen());
        existente.setUbicacion(almacen.getUbicacion());
        existente.setCapacidadTotal(almacen.getCapacidadTotal());
        return almacenRepository.save(existente);
    }
    
    @Transactional(readOnly = true)
    public Almacen obtenerAlmacenPorId(Integer id) {
        return almacenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Almacen> obtenerAlmacenesActivos() {
        return almacenRepository.findByEstado(Almacen.EstadoAlmacen.ACTIVO);
    }
}
