package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    
    // Buscar detalles por venta
    List<DetalleVenta> findByVenta(Venta venta);
    
    // Buscar detalles por ID de venta
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.idVenta = :ventaId ORDER BY d.idDetalle ASC")
    List<DetalleVenta> findByVentaId(@Param("ventaId") Integer ventaId);
    
    // Buscar detalles por producto
    @Query("SELECT d FROM DetalleVenta d WHERE d.producto.idProducto = :productoId")
    List<DetalleVenta> findByProductoId(@Param("productoId") Integer productoId);
    
    // Productos más vendidos en un período
    @Query("SELECT d.producto, SUM(d.cantidad) as totalCantidad, COUNT(d) as numeroVentas, SUM(d.total) as totalIngresos " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY d.producto " +
           "ORDER BY totalCantidad DESC")
    List<Object[]> findProductosMasVendidos(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Top 10 productos más vendidos
    @Query(value = "SELECT d.producto, SUM(d.cantidad) as total " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY d.producto " +
           "ORDER BY total DESC",
           nativeQuery = false)
    List<Object[]> findTop10ProductosMasVendidos(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Total de unidades vendidas de un producto
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE d.producto.idProducto = :productoId " +
           "AND v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTotalUnidadesVendidasPorProducto(
        @Param("productoId") Integer productoId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Total de ingresos por producto
    @Query("SELECT COALESCE(SUM(d.total), 0) " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE d.producto.idProducto = :productoId " +
           "AND v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTotalIngresosPorProducto(
        @Param("productoId") Integer productoId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Cantidad total de productos vendidos en un período
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTotalUnidadesVendidas(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Promedio de unidades por venta
    @Query("SELECT AVG(d.cantidad) " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getPromedioUnidadesPorVenta(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Productos vendidos por categoría
    @Query("SELECT p.categoria, COUNT(DISTINCT d.producto), SUM(d.cantidad), SUM(d.total) " +
           "FROM DetalleVenta d " +
           "JOIN d.producto p " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY p.categoria " +
           "ORDER BY SUM(d.total) DESC")
    List<Object[]> findVentasPorCategoria(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Productos con mayor margen de ganancia
    @Query("SELECT d.producto, " +
           "SUM(d.cantidad) as cantidad, " +
           "SUM(d.total - (p.precioCompra * d.cantidad)) as ganancia " +
           "FROM DetalleVenta d " +
           "JOIN d.producto p " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY d.producto " +
           "ORDER BY ganancia DESC")
    List<Object[]> findProductosMayorGanancia(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Total de descuentos aplicados en un período
    @Query("SELECT COALESCE(SUM(d.descuento), 0) " +
           "FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTotalDescuentosAplicados(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Productos vendidos hoy
    @Query("SELECT d FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND DATE(v.fechaVenta) = CURRENT_DATE " +
           "ORDER BY v.fechaVenta DESC")
    List<DetalleVenta> findDetallesVentasHoy();
    
    // Contar cuántas veces se ha vendido un producto
    @Query("SELECT COUNT(d) FROM DetalleVenta d " +
           "JOIN d.venta v " +
           "WHERE d.producto.idProducto = :productoId " +
           "AND v.estado = 'PAGADA'")
    Long countVentasByProducto(@Param("productoId") Integer productoId);
}