package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface AperturaCajaRepository extends JpaRepository<AperturaCaja, Integer> {
    
    List<AperturaCaja> findByCaja(Caja caja);
    

    List<AperturaCaja> findByEstado(AperturaCaja.EstadoApertura estado);
    

    List<AperturaCaja> findByUsuario(Usuario usuario);
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.caja.idCaja = :cajaId AND a.estado = 'ABIERTA'")
    Optional<AperturaCaja> findCajaAbierta(@Param("cajaId") Integer cajaId);
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.usuario.id = :usuarioId AND a.estado = 'ABIERTA'")
    Optional<AperturaCaja> findAperturaAbiertaPorUsuario(@Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.fechaApertura BETWEEN :inicio AND :fin " +
           "ORDER BY a.fechaApertura DESC")
    List<AperturaCaja> findByFechaAperturaBetween(
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.estado = 'CERRADA' " +
           "AND a.fechaCierre BETWEEN :inicio AND :fin " +
           "ORDER BY a.fechaCierre DESC")
    List<AperturaCaja> findAperturasCerradasEnPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT a FROM AperturaCaja a WHERE DATE(a.fechaApertura) = CURRENT_DATE " +
           "ORDER BY a.fechaApertura DESC")
    List<AperturaCaja> findAperturasDelDia();
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.estado = 'ABIERTA' " +
           "ORDER BY a.fechaApertura DESC")
    List<AperturaCaja> findAperturasAbiertas();
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.caja.idCaja = :cajaId " +
           "ORDER BY a.fechaApertura DESC LIMIT 1")
    Optional<AperturaCaja> findUltimaAperturaDeCaja(@Param("cajaId") Integer cajaId);
    
    @Query("SELECT COALESCE(SUM(a.totalIngresos), 0) FROM AperturaCaja a " +
           "WHERE a.estado = 'CERRADA' " +
           "AND a.fechaCierre BETWEEN :inicio AND :fin")
    BigDecimal getTotalIngresosEnPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT COALESCE(SUM(a.totalEgresos), 0) FROM AperturaCaja a " +
           "WHERE a.estado = 'CERRADA' " +
           "AND a.fechaCierre BETWEEN :inicio AND :fin")
    BigDecimal getTotalEgresosEnPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.estado = 'CERRADA' " +
           "AND a.saldoReal IS NOT NULL " +
           "AND (a.saldoReal - (a.saldoInicial + a.totalIngresos - a.totalEgresos)) != 0 " +
           "ORDER BY a.fechaCierre DESC")
    List<AperturaCaja> findAperturasConDiferencias();
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.estado = 'CERRADA' " +
           "AND a.saldoReal IS NOT NULL " +
           "AND (a.saldoReal - (a.saldoInicial + a.totalIngresos - a.totalEgresos)) < 0 " +
           "ORDER BY a.fechaCierre DESC")
    List<AperturaCaja> findAperturasConFaltante();
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.estado = 'CERRADA' " +
           "AND a.saldoReal IS NOT NULL " +
           "AND (a.saldoReal - (a.saldoInicial + a.totalIngresos - a.totalEgresos)) > 0 " +
           "ORDER BY a.fechaCierre DESC")
    List<AperturaCaja> findAperturasConSobrante();
    
    @Query("SELECT COUNT(a) FROM AperturaCaja a WHERE a.estado = :estado")
    Long countByEstado(@Param("estado") AperturaCaja.EstadoApertura estado);
    
    @Query("SELECT a FROM AperturaCaja a WHERE a.usuario.id = :usuarioId " +
           "AND a.fechaApertura BETWEEN :inicio AND :fin " +
           "ORDER BY a.fechaApertura DESC")
    List<AperturaCaja> findByUsuarioAndPeriodo(
        @Param("usuarioId") Integer usuarioId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM AperturaCaja a WHERE a.caja.idCaja = :cajaId AND a.estado = 'ABIERTA'")
    boolean existeAperturaAbierta(@Param("cajaId") Integer cajaId);
    

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM AperturaCaja a WHERE a.usuario.id = :usuarioId AND a.estado = 'ABIERTA'")
    boolean existeAperturaAbiertaParaUsuario(@Param("usuarioId") Integer usuarioId);
    
    @Query("SELECT AVG(a.saldoReal - (a.saldoInicial + a.totalIngresos - a.totalEgresos)) " +
           "FROM AperturaCaja a WHERE a.estado = 'CERRADA' " +
           "AND a.fechaCierre BETWEEN :inicio AND :fin")
    BigDecimal getPromedioDiferencias(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}
