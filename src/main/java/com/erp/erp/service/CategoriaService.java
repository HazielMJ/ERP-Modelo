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
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    
    public Categoria crearCategoria(Categoria categoria) {
        if (categoriaRepository.findByCodigoCategoria(categoria.getCodigoCategoria()).isPresent()) {
            throw new RuntimeException("Ya existe una categoría con ese código");
        }
        return categoriaRepository.save(categoria);
    }
    
    public Categoria actualizarCategoria(Integer id, Categoria categoria) {
        Categoria existente = obtenerCategoriaPorId(id);
        existente.setNombreCategoria(categoria.getNombreCategoria());
        existente.setDescripcion(categoria.getDescripcion());
        existente.setCategoriaPadre(categoria.getCategoriaPadre());
        return categoriaRepository.save(existente);
    }
    
    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
    
    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategoriasActivas() {
        return categoriaRepository.findByEstado(Categoria.EstadoCategoria.ACTIVA);
    }
}
