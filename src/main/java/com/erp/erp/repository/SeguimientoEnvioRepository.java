package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeguimientoEnvioRepository extends JpaRepository<SeguimientoEnvio, Integer> {
    List<SeguimientoEnvio> findByEnvio(Envio envio);
    
    @Query("SELECT s FROM SeguimientoEnvio s WHERE s.envio.id = :envioId ORDER BY s.fechaHora DESC")
    List<SeguimientoEnvio> findByEnvioIdOrderByFechaDesc(@Param("envioId") Integer envioId);
    
    @Query("SELECT s FROM SeguimientoEnvio s WHERE s.estadoEnvio = :estado")
    List<SeguimientoEnvio> findByEstadoEnvio(@Param("estado") SeguimientoEnvio.EstadoSeguimiento estado);
    
    @Query("SELECT s FROM SeguimientoEnvio s WHERE s.envio.id = :envioId AND s.estadoEnvio = :estado")
    List<SeguimientoEnvio> findByEnvioIdAndEstado(@Param("envioId") Integer envioId, @Param("estado") SeguimientoEnvio.EstadoSeguimiento estado);
    
    @Query("SELECT s FROM SeguimientoEnvio s WHERE s.fechaHora BETWEEN :inicio AND :fin ORDER BY s.fechaHora DESC")
    List<SeguimientoEnvio> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT s FROM SeguimientoEnvio s WHERE s.usuario.id = :usuarioId ORDER BY s.fechaHora DESC")
    List<SeguimientoEnvio> findByUsuarioId(@Param("usuarioId") Integer usuarioId);
}
