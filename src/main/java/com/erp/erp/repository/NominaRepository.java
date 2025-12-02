package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public interface NominaRepository extends JpaRepository<Nomina, Integer> {
    List<Nomina> findByEmpleado(Empleado empleado);
    List<Nomina> findByEstado(Nomina.EstadoNomina estado);
    List<Nomina> findByPeriodoNomina(String periodo);
    
    @Query("SELECT n FROM Nomina n WHERE n.fechaPago BETWEEN :inicio AND :fin")
    List<Nomina> findByFechaPagoBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT SUM(n.totalNeto) FROM Nomina n WHERE n.estado = 'PAGADA' AND n.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal getTotalNominaByPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
