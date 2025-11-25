package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
    Optional<Departamento> findByCodigoDepartamento(String codigo);
    List<Departamento> findByEstado(Departamento.EstadoDepartamento estado);
    List<Departamento> findByDepartamentoPadre(Departamento padre);
    List<Departamento> findByNombreDepartamentoContaining(String nombre);
}

