package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByCodigoCategoria(String codigo);
    List<Categoria> findByEstado(Categoria.EstadoCategoria estado);
    List<Categoria> findByCategoriaPadre(Categoria padre);
    List<Categoria> findByNombreCategoriaContaining(String nombre);
}
