package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByCodigoProducto(String codigo);
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    List<Producto> findByEstado(Producto.EstadoProducto estado);
    List<Producto> findByCategoria(Categoria categoria);
    List<Producto> findByNombreProductoContaining(String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'ACTIVO'")
    List<Producto> findAllActive();
    
    @Query("SELECT p FROM Producto p JOIN Inventario i ON p.idProducto = i.producto.id WHERE i.stockActual <= p.stockMinimo")
    List<Producto> findProductosBajoStock();

    // Agregar este método al final de la interfaz
@Query("SELECT p FROM Producto p WHERE (p.codigoBarras IS NULL OR p.codigoBarras = '') AND p.estado = 'ACTIVO'")
List<Producto> findProductosSinCodigoBarras();
}
