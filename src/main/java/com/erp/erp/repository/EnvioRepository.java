package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    
    /**
     * 🔥 CARGA TODAS LAS RELACIONES DE UNA VEZ - SOLUCIONA EL ERROR
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    @Query("SELECT e FROM Envio e")
    List<Envio> findAllWithRelations();
    
    /**
     * Buscar por ID con relaciones cargadas
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    Optional<Envio> findById(Integer id);
    
    /**
     * Buscar por número de guía con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    Optional<Envio> findByNumeroGuia(String numeroGuia);
    
    /**
     * Buscar por estado con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    List<Envio> findByEstado(Envio.EstadoEnvio estado);
    
    /**
     * Buscar por cliente con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    List<Envio> findByCliente(Cliente cliente);
    
    /**
     * Buscar por venta con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    List<Envio> findByVenta(Venta venta);
    
    /**
     * Buscar por rango de fechas con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    @Query("SELECT e FROM Envio e WHERE e.fechaCreacion BETWEEN :inicio AND :fin")
    List<Envio> findByFechaCreacionBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    /**
     * Envíos en proceso con relaciones
     */
    @EntityGraph(attributePaths = {"cliente", "metodoEnvio", "ruta", "transportista", "venta"})
    @Query("SELECT e FROM Envio e WHERE e.estado IN ('PENDIENTE', 'PREPARANDO', 'EN_TRANSITO')")
    List<Envio> findEnviosEnProceso();
}
