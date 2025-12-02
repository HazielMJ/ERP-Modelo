package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, Integer> {
    List<Impuesto> findByTipo(Impuesto.TipoImpuesto tipo);
    List<Impuesto> findByEstado(Impuesto.EstadoImpuesto estado);
}
