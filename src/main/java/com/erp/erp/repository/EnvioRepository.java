package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
    Optional<Envio> findByNumeroGuia(String numeroGuia);
    List<Envio> findByEstado(Envio.EstadoEnvio estado);
    List<Envio> findByCliente(Cliente cliente);
    List<Envio> findByVenta(Venta venta);
    
    @Query("SELECT e FROM Envio e WHERE e.fechaCreacion BETWEEN :inicio AND :fin")
    List<Envio> findByFechaCreacionBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT e FROM Envio e WHERE e.estado IN ('PENDIENTE', 'PREPARANDO', 'EN_TRANSITO')")
    List<Envio> findEnviosEnProceso();
}
