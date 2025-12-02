package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    Optional<Ruta> findByCodigoRuta(String codigo);
    List<Ruta> findByEstado(Ruta.EstadoRuta estado);
    List<Ruta> findByOrigenAndDestino(String origen, String destino);
}
