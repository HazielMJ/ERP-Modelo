package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {
    List<DetalleCompra> findByCompra(Compra compra);
    
    @Query("SELECT d FROM DetalleCompra d WHERE d.compra.id = :compraId")
    List<DetalleCompra> findByCompraId(@Param("compraId") Integer compraId);
}
