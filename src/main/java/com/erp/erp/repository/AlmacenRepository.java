package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Integer> {
    Optional<Almacen> findByCodigoAlmacen(String codigo);
    List<Almacen> findByEstado(Almacen.EstadoAlmacen estado);
    List<Almacen> findByNombreAlmacenContaining(String nombre);
}
