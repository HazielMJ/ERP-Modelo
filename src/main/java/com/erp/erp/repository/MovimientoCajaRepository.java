package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {
    
    // Buscar por apertura
    List<MovimientoCaja> findByApertura(AperturaCaja apertura);
    
    // Buscar por tipo de movimiento
    List<MovimientoCaja> findByTipoMovimiento(MovimientoCaja.TipoMovimiento tipo);
    
    // Buscar por categoría
    List<MovimientoCaja> findByCategoria(MovimientoCaja.CategoriaMovimiento categoria);
    
    // Buscar por usuario
    List<MovimientoCaja> findByUsuario(Usuario usuario);
    
    // Buscar por venta
    List<MovimientoCaja> findByVenta(Venta venta);
    
    // Buscar movimientos de una apertura ordenados por fecha
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findByAperturaIdOrderByFecha(@Param("aperturaId") Integer aperturaId);
    
    // Total por apertura y tipo
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId AND m.tipoMovimiento = :tipo")
    BigDecimal getTotalByAperturaAndTipo(
        @Param("aperturaId") Integer aperturaId, 
        @Param("tipo") MovimientoCaja.TipoMovimiento tipo
    );
    
    // Total ingresos de una apertura
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId AND m.tipoMovimiento = 'INGRESO'")
    BigDecimal getTotalIngresosByApertura(@Param("aperturaId") Integer aperturaId);
    
    // Total egresos de una apertura
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId AND m.tipoMovimiento = 'EGRESO'")
    BigDecimal getTotalEgresosByApertura(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos por categoría en una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.categoria = :categoria ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findByAperturaAndCategoria(
        @Param("aperturaId") Integer aperturaId,
        @Param("categoria") MovimientoCaja.CategoriaMovimiento categoria
    );
    
    // Movimientos por forma de pago en una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.formaPago = :formaPago ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findByAperturaAndFormaPago(
        @Param("aperturaId") Integer aperturaId,
        @Param("formaPago") MovimientoCaja.FormaPago formaPago
    );
    
    // Total por forma de pago en una apertura
    @Query("SELECT m.formaPago, COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.tipoMovimiento = 'INGRESO' " +
           "GROUP BY m.formaPago")
    List<Object[]> getTotalPorFormaPago(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos de ventas en una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.categoria = 'VENTA' AND m.venta IS NOT NULL " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findVentasByApertura(@Param("aperturaId") Integer aperturaId);
    
    // Total de ventas en efectivo
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.categoria = 'VENTA' AND m.formaPago = 'EFECTIVO'")
    BigDecimal getTotalVentasEfectivo(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos de retiros en una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.categoria = 'RETIRO' ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findRetirosByApertura(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos de gastos en una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.categoria = 'GASTO' ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findGastosByApertura(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos por rango de fechas
    @Query("SELECT m FROM MovimientoCaja m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findByFechaMovimientoBetween(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Movimientos del día
    @Query("SELECT m FROM MovimientoCaja m WHERE DATE(m.fechaMovimiento) = CURRENT_DATE " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findMovimientosDelDia();
    
    // Contar movimientos por tipo
    @Query("SELECT COUNT(m) FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "AND m.tipoMovimiento = :tipo")
    Long countByAperturaAndTipo(
        @Param("aperturaId") Integer aperturaId,
        @Param("tipo") MovimientoCaja.TipoMovimiento tipo
    );
    
    // Estadísticas por categoría
    @Query("SELECT m.categoria, COUNT(m), COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.apertura.idApertura = :aperturaId " +
           "GROUP BY m.categoria " +
           "ORDER BY SUM(m.monto) DESC")
    List<Object[]> getEstadisticasPorCategoria(@Param("aperturaId") Integer aperturaId);
    
    // Último movimiento de una apertura
    @Query("SELECT m FROM MovimientoCaja m WHERE m.apertura.idApertura = :aperturaId " +
           "ORDER BY m.fechaMovimiento DESC LIMIT 1")
    MovimientoCaja findUltimoMovimiento(@Param("aperturaId") Integer aperturaId);
    
    // Movimientos de un usuario en un período
    @Query("SELECT m FROM MovimientoCaja m WHERE m.usuario.id = :usuarioId " +
           "AND m.fechaMovimiento BETWEEN :inicio AND :fin " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findByUsuarioAndPeriodo(
        @Param("usuarioId") Integer usuarioId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Total de movimientos por usuario
    @Query("SELECT m.tipoMovimiento, COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m " +
           "WHERE m.usuario.id = :usuarioId " +
           "AND m.fechaMovimiento BETWEEN :inicio AND :fin " +
           "GROUP BY m.tipoMovimiento")
    List<Object[]> getTotalPorUsuarioYTipo(
        @Param("usuarioId") Integer usuarioId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    // Verificar si existe movimiento para una venta
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MovimientoCaja m WHERE m.venta.idVenta = :ventaId")
    boolean existeMovimientoParaVenta(@Param("ventaId") Integer ventaId);
}