package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Integer> {
    Optional<Puesto> findByCodigoPuesto(String codigo);
    List<Puesto> findByEstado(Puesto.EstadoPuesto estado);
    List<Puesto> findByNombrePuestoContaining(String nombre);
}
