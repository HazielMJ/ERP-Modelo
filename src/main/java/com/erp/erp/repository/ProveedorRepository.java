package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByEstado(Proveedor.EstadoProveedor estado);
    List<Proveedor> findByNombreEmpresaContaining(String nombre);
    Optional<Proveedor> findByRfc(String rfc);
    
    @Query("SELECT p FROM Proveedor p WHERE p.estado = 'ACTIVO' AND p.saldoActual > p.limiteCredito")
    List<Proveedor> findProveedoresConSaldoExcedido();
}
