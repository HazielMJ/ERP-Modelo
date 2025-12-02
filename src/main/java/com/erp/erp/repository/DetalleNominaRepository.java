package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleNominaRepository extends JpaRepository<DetalleNomina, Integer> {
    List<DetalleNomina> findByNomina(Nomina nomina);
    
    @Query("SELECT d FROM DetalleNomina d WHERE d.nomina.id = :nominaId AND d.tipo = :tipo")
    List<DetalleNomina> findByNominaIdAndTipo(@Param("nominaId") Integer nominaId, @Param("tipo") DetalleNomina.TipoConcepto tipo);
}
