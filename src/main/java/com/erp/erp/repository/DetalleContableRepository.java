package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;

@Repository
public interface DetalleContableRepository extends JpaRepository<DetalleContable, Integer> {
    List<DetalleContable> findByAsiento(Contabilidad asiento);
    
    @Query("SELECT d FROM DetalleContable d WHERE d.asiento.id = :asientoId ORDER BY d.numeroLinea")
    List<DetalleContable> findByAsientoIdOrderByNumeroLinea(@Param("asientoId") Integer asientoId);
    
    @Query("SELECT d FROM DetalleContable d WHERE d.cuentaContable = :cuenta")
    List<DetalleContable> findByCuentaContable(@Param("cuenta") String cuenta);
    
    @Query("SELECT SUM(d.debe) FROM DetalleContable d WHERE d.asiento.id = :asientoId")
    BigDecimal getTotalDebeByAsiento(@Param("asientoId") Integer asientoId);
    
    @Query("SELECT SUM(d.haber) FROM DetalleContable d WHERE d.asiento.id = :asientoId")
    BigDecimal getTotalHaberByAsiento(@Param("asientoId") Integer asientoId);
}
