package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MetodoEnvioService {
    private final MetodoEnvioRepository metodoEnvioRepository;
    
    public MetodoEnvio crearMetodoEnvio(MetodoEnvio metodo) {
        return metodoEnvioRepository.save(metodo);
    }
    
    public MetodoEnvio guardar(MetodoEnvio metodo) {
        return metodoEnvioRepository.save(metodo);
    }
    
    public MetodoEnvio actualizarMetodoEnvio(Integer id, MetodoEnvio metodo) {
        MetodoEnvio existente = obtenerMetodoEnvioPorId(id);
        existente.setNombre(metodo.getNombre());
        existente.setDescripcion(metodo.getDescripcion());
        existente.setCostoBase(metodo.getCostoBase());
        existente.setCostoPorKm(metodo.getCostoPorKm());
        existente.setTiempoEstimado(metodo.getTiempoEstimado());
        existente.setEstado(metodo.getEstado());
        return metodoEnvioRepository.save(existente);
    }
    
    public void eliminar(Integer id) {
        metodoEnvioRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public MetodoEnvio obtenerMetodoEnvioPorId(Integer id) {
        return metodoEnvioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Método de envío no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Optional<MetodoEnvio> buscarPorId(Integer id) {
        return metodoEnvioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<MetodoEnvio> listar() {
        return metodoEnvioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<MetodoEnvio> obtenerMetodosActivos() {
        return metodoEnvioRepository.findByEstado(MetodoEnvio.EstadoMetodo.ACTIVO);
    }
    
    @Transactional(readOnly = true)
    public List<MetodoEnvio> buscarPorEstado(MetodoEnvio.EstadoMetodo estado) {
        return metodoEnvioRepository.findByEstado(estado);
    }
    
    @Transactional(readOnly = true)
    public List<MetodoEnvio> buscarPorNombre(String nombre) {
        return metodoEnvioRepository.findByNombreContaining(nombre);
    }
}
