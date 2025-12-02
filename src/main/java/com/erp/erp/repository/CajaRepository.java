package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Integer> {
    
    // Buscar por número de caja
    Optional<Caja> findByNumeroCaja(String numeroCaja);
    
    // Buscar por nombre
    Optional<Caja> findByNombreCaja(String nombreCaja);
    
    // Buscar por estado
    List<Caja> findByEstado(Caja.EstadoCaja estado);
    
    // Buscar cajas activas
    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' ORDER BY c.nombreCaja")
    List<Caja> findCajasActivas();
    
    // Buscar cajas disponibles (activas sin apertura)
    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' " +
           "AND c.idCaja NOT IN (SELECT a.caja.idCaja FROM AperturaCaja a WHERE a.estado = 'ABIERTA')")
    List<Caja> findCajasDisponibles();
    
    // Verificar si existe una caja con ese número
    boolean existsByNumeroCaja(String numeroCaja);
    
    // Verificar si existe una caja con ese nombre
    boolean existsByNombreCaja(String nombreCaja);
    
    // Buscar por ubicación
    List<Caja> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    // Contar cajas por estado
    @Query("SELECT COUNT(c) FROM Caja c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Caja.EstadoCaja estado);
    
    // Obtener cajas con aperturas abiertas
    @Query("SELECT DISTINCT c FROM Caja c " +
           "JOIN AperturaCaja a ON a.caja.idCaja = c.idCaja " +
           "WHERE a.estado = 'ABIERTA'")
    List<Caja> findCajasConAperturaAbierta();
    
    // Obtener cajas sin aperturas en el día
    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' " +
           "AND c.idCaja NOT IN (" +
           "  SELECT a.caja.idCaja FROM AperturaCaja a " +
           "  WHERE DATE(a.fechaApertura) = CURRENT_DATE" +
           ")")
    List<Caja> findCajasSinAperturaHoy();
}