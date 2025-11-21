package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    Optional<Compra> findByNumeroCompra(String numeroCompra);
    List<Compra> findByEstado(Compra.EstadoCompra estado);
    List<Compra> findByProveedor(Proveedor proveedor);
    
    @Query("SELECT c FROM Compra c WHERE c.fechaCompra BETWEEN :inicio AND :fin")
    List<Compra> findByFechaCompraBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT SUM(c.totalCompra) FROM Compra c WHERE c.estado = 'RECIBIDA' AND c.fechaCompra BETWEEN :inicio AND :fin")
    BigDecimal getTotalComprasByPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
