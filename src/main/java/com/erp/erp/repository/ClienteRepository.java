package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByEstado(Cliente.EstadoCliente estado);
    List<Cliente> findByTipoCliente(Cliente.TipoCliente tipoCliente);
    List<Cliente> findByNombreRazonSocialContaining(String nombre);
    Optional<Cliente> findByRfc(String rfc);
    
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'ACTIVO' AND c.saldoActual > c.limiteCredito")
    List<Cliente> findClientesConSaldoExcedido();
    
    @Query("SELECT c FROM Cliente c WHERE c.estado = 'ACTIVO' ORDER BY c.saldoActual DESC")
    List<Cliente> findTopClientesBySaldo();
}
