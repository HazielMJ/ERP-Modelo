package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MetodoEnvioRepository extends JpaRepository<MetodoEnvio, Integer> {
    List<MetodoEnvio> findByEstado(MetodoEnvio.EstadoMetodo estado);
    List<MetodoEnvio> findByNombreContaining(String nombre);
}
