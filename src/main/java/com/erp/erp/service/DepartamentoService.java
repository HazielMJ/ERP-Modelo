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
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepository;
    
    public Departamento crearDepartamento(Departamento departamento) {
        if (departamentoRepository.findByCodigoDepartamento(departamento.getCodigoDepartamento()).isPresent()) {
            throw new RuntimeException("Ya existe un departamento con ese código");
        }
        return departamentoRepository.save(departamento);
    }
    
    public Departamento actualizarDepartamento(Integer id, Departamento departamento) {
        Departamento existente = obtenerDepartamentoPorId(id);
        existente.setNombreDepartamento(departamento.getNombreDepartamento());
        existente.setDescripcion(departamento.getDescripcion());
        existente.setDepartamentoPadre(departamento.getDepartamentoPadre());
        existente.setJefe(departamento.getJefe());
        return departamentoRepository.save(existente);
    }
    
    @Transactional(readOnly = true)
    public Departamento obtenerDepartamentoPorId(Integer id) {
        return departamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Departamento> obtenerTodosLosDepartamentos() {
        return departamentoRepository.findAll();
    }
}
