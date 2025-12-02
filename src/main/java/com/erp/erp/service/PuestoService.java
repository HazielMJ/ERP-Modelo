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
public class PuestoService {
    private final PuestoRepository puestoRepository;
    
    public Puesto crearPuesto(Puesto puesto) {
        if (puestoRepository.findByCodigoPuesto(puesto.getCodigoPuesto()).isPresent()) {
            throw new RuntimeException("Ya existe un puesto con ese código");
        }
        return puestoRepository.save(puesto);
    }
    
    public Puesto actualizarPuesto(Integer id, Puesto puesto) {
        Puesto existente = obtenerPuestoPorId(id);
        existente.setNombrePuesto(puesto.getNombrePuesto());
        existente.setDescripcion(puesto.getDescripcion());
        existente.setNivelJerarquico(puesto.getNivelJerarquico());
        existente.setSalarioMinimo(puesto.getSalarioMinimo());
        existente.setSalarioMaximo(puesto.getSalarioMaximo());
        return puestoRepository.save(existente);
    }
    
    @Transactional(readOnly = true)
    public Puesto obtenerPuestoPorId(Integer id) {
        return puestoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Puesto> obtenerTodosLosPuestos() {
        return puestoRepository.findAll();
    }
}
