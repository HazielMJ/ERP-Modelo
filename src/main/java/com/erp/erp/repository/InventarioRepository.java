package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    List<Inventario> findByProducto(Producto producto);
    List<Inventario> findByAlmacen(Almacen almacen);
    Optional<Inventario> findByProductoAndAlmacen(Producto producto, Almacen almacen);
    
    @Query("SELECT i FROM Inventario i WHERE i.producto.id = :productoId AND i.almacen.id = :almacenId")
    Optional<Inventario> findByProductoIdAndAlmacenId(@Param("productoId") Integer productoId, @Param("almacenId") Integer almacenId);
    
    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.producto.stockMinimo")
    List<Inventario> findInventarioBajoStock();
    
    // ⭐ NUEVO: Obtener stock total disponible por producto (suma de todos los almacenes)
    @Query("SELECT COALESCE(SUM(i.stockActual - i.stockReservado), 0) " +
           "FROM Inventario i WHERE i.producto.id = :productoId")
    Integer getStockDisponibleByProducto(@Param("productoId") Integer productoId);
}
