package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ContabilidadRepository extends JpaRepository<Contabilidad, Integer> {
    Optional<Contabilidad> findByNumeroAsiento(String numeroAsiento);
    List<Contabilidad> findByEstado(Contabilidad.EstadoAsiento estado);
    List<Contabilidad> findByPeriodoContable(String periodoContable);
    
    @Query("SELECT c FROM Contabilidad c WHERE c.fechaAsiento BETWEEN :inicio AND :fin")
    List<Contabilidad> findByFechaAsientoBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT c FROM Contabilidad c WHERE c.usuario.id = :userId AND c.estado = :estado")
    List<Contabilidad> findByUsuarioAndEstado(@Param("userId") Integer userId, @Param("estado") Contabilidad.EstadoAsiento estado);
}
