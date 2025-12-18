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
    

    Optional<Caja> findByNumeroCaja(String numeroCaja);

    Optional<Caja> findByNombreCaja(String nombreCaja);
    

    List<Caja> findByEstado(Caja.EstadoCaja estado);
    
    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' ORDER BY c.nombreCaja")
    List<Caja> findCajasActivas();
    

    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' " +
           "AND c.idCaja NOT IN (SELECT a.caja.idCaja FROM AperturaCaja a WHERE a.estado = 'ABIERTA')")
    List<Caja> findCajasDisponibles();
    
    boolean existsByNumeroCaja(String numeroCaja);
    
    boolean existsByNombreCaja(String nombreCaja);
    
    List<Caja> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    @Query("SELECT COUNT(c) FROM Caja c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Caja.EstadoCaja estado);
    
    @Query("SELECT DISTINCT c FROM Caja c " +
           "JOIN AperturaCaja a ON a.caja.idCaja = c.idCaja " +
           "WHERE a.estado = 'ABIERTA'")
    List<Caja> findCajasConAperturaAbierta();
    
    @Query("SELECT c FROM Caja c WHERE c.estado = 'ACTIVA' " +
           "AND c.idCaja NOT IN (" +
           "  SELECT a.caja.idCaja FROM AperturaCaja a " +
           "  WHERE DATE(a.fechaApertura) = CURRENT_DATE" +
           ")")
    List<Caja> findCajasSinAperturaHoy();
}
