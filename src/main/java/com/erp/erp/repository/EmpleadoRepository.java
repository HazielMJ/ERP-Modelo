package com.erp.erp.repository;

import com.erp.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    @Query("SELECT e FROM Empleado e " +
           "LEFT JOIN FETCH e.puesto " +
           "LEFT JOIN FETCH e.departamento " +
           "WHERE e.estado = :estado")
    List<Empleado> findByEstadoWithRelations(@Param("estado") Empleado.EstadoEmpleado estado);
    
    @Query("SELECT e FROM Empleado e " +
           "LEFT JOIN FETCH e.puesto " +
           "LEFT JOIN FETCH e.departamento " +
           "WHERE e.idEmpleado = :id")
    Optional<Empleado> findByIdWithRelations(@Param("id") Integer id);
    
    @Query("SELECT e FROM Empleado e " +
           "LEFT JOIN FETCH e.puesto " +
           "LEFT JOIN FETCH e.departamento " +
           "WHERE e.departamento = :departamento")
    List<Empleado> findByDepartamentoWithRelations(@Param("departamento") Departamento departamento);
    
    Optional<Empleado> findByCodigoEmpleado(String codigo);
    List<Empleado> findByEstado(Empleado.EstadoEmpleado estado);
    List<Empleado> findByDepartamento(Departamento departamento);
    List<Empleado> findByPuesto(Puesto puesto);
    Optional<Empleado> findByRfc(String rfc);
    Optional<Empleado> findByCurp(String curp);
    
    @Query("SELECT e FROM Empleado e WHERE CONCAT(e.nombre, ' ', e.apellido) LIKE %:nombre%")
    List<Empleado> findByNombreCompleto(@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Empleado e WHERE e.estado = 'ACTIVO' AND e.departamento.idDepartamento = :deptoId")
    List<Empleado> findEmpleadosActivosByDepartamento(@Param("deptoId") Integer deptoId);
}
