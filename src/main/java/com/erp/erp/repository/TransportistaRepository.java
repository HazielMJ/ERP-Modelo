package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Integer> {
    List<Transportista> findByEstado(Transportista.EstadoTransportista estado);
    List<Transportista> findByNombreContaining(String nombre);
    Optional<Transportista> findByLicencia(String licencia);
}
