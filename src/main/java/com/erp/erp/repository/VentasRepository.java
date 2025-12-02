package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Repository
public interface VentasRepository extends JpaRepository<Venta, Integer> {
    
    // Búsqueda por número de venta
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    
    // Búsqueda por estado
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    
    // Búsqueda por cliente
    List<Venta> findByCliente(Cliente cliente);
    
    // Búsqueda por usuario
    List<Venta> findByUsuario(Usuario usuario);
    
    // Búsqueda por tipo de pago
    List<Venta> findByTipoPago(Venta.TipoPago tipoPago);
    
    // Búsqueda por rango de fechas
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin ORDER BY v.fechaVenta DESC")
    List<Venta> findByFechaVentaBetween(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Total de ventas pagadas por período
    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v " +
           "WHERE v.estado = 'PAGADA' AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTotalVentasByPeriodo(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    // Ventas por cliente y estado
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :clienteId AND v.estado = :estado")
    List<Venta> findByClienteIdAndEstado(
        @Param("clienteId") Integer clienteId, 
        @Param("estado") Venta.EstadoVenta estado
    );
    
    // Contar ventas por estado
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = :estado")
    Long countByEstado(@Param("estado") Venta.EstadoVenta estado);
    
    // Ventas del día
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelDia();
    
    // Ventas del mes
    @Query("SELECT v FROM Venta v WHERE MONTH(v.fechaVenta) = MONTH(CURRENT_DATE) " +
           "AND YEAR(v.fechaVenta) = YEAR(CURRENT_DATE) ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelMes();
    
    // Total de ventas del día
    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v " +
           "WHERE v.estado = 'PAGADA' AND DATE(v.fechaVenta) = CURRENT_DATE")
    BigDecimal getTotalVentasDelDia();
    
    // Total de ventas del mes
    @Query("SELECT COALESCE(SUM(v.totalVenta), 0) FROM Venta v " +
           "WHERE v.estado = 'PAGADA' AND MONTH(v.fechaVenta) = MONTH(CURRENT_DATE) " +
           "AND YEAR(v.fechaVenta) = YEAR(CURRENT_DATE)")
    BigDecimal getTotalVentasDelMes();
    
    // Buscar ventas pendientes de un cliente
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :clienteId " +
           "AND v.estado = 'PENDIENTE' ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasPendientesByCliente(@Param("clienteId") Integer clienteId);
    
    // Buscar ventas a crédito pendientes
    @Query("SELECT v FROM Venta v WHERE v.tipoPago = 'CREDITO' " +
           "AND v.estado = 'PENDIENTE' ORDER BY v.fechaVenta ASC")
    List<Venta> findVentasCreditoPendientes();
    
    // Ventas por usuario en un período
    @Query("SELECT v FROM Venta v WHERE v.usuario.id = :usuarioId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin ORDER BY v.fechaVenta DESC")
    List<Venta> findByUsuarioAndPeriodo(
        @Param("usuarioId") Integer usuarioId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Top productos más vendidos
    @Query("SELECT dv.producto, SUM(dv.cantidad) as totalVendido " +
           "FROM DetalleVenta dv " +
           "JOIN dv.venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY dv.producto " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findTopProductosMasVendidos(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Estadísticas por método de pago
    @Query("SELECT v.tipoPago, COUNT(v), COALESCE(SUM(v.totalVenta), 0) " +
           "FROM Venta v " +
           "WHERE v.estado = 'PAGADA' " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY v.tipoPago")
    List<Object[]> findEstadisticasPorMetodoPago(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Ticket promedio
    @Query("SELECT AVG(v.totalVenta) FROM Venta v " +
           "WHERE v.estado = 'PAGADA' AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal getTicketPromedio(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}