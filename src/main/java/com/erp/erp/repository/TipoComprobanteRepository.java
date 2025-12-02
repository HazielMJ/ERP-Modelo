package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Integer> {
    Optional<TipoComprobante> findByCodigo(String codigo);
    List<TipoComprobante> findByEstado(TipoComprobante.EstadoComprobante estado);
}
