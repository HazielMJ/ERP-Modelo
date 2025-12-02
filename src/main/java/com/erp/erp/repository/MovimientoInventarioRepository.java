package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {
    List<MovimientoInventario> findByProducto(Producto producto);
    List<MovimientoInventario> findByTipoMovimiento(MovimientoInventario.TipoMovimiento tipo);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin")
    List<MovimientoInventario> findByFechaMovimientoBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.producto.id = :productoId ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByProductoIdOrderByFechaDesc(@Param("productoId") Integer productoId);
}
