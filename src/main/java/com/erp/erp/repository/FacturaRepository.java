package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    List<Factura> findByEstado(Factura.EstadoFactura estado);
    List<Factura> findByCliente(Cliente cliente);
    List<Factura> findByTipoFactura(Factura.TipoFactura tipoFactura);
    
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :inicio AND :fin")
    List<Factura> findByFechaEmisionBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT f FROM Factura f WHERE f.estado = 'PENDIENTE' AND f.fechaVencimiento < :fecha")
    List<Factura> findFacturasVencidas(@Param("fecha") LocalDate fecha);
}
