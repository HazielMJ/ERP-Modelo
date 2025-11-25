package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PuestoRepository puestoRepository;
    
    public Empleado crearEmpleado(Empleado empleado) {
        if (empleado.getRfc() != null && empleadoRepository.findByRfc(empleado.getRfc()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con ese RFC");
        }
        if (empleado.getCurp() != null && empleadoRepository.findByCurp(empleado.getCurp()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con ese CURP");
        }
        empleado.setCodigoEmpleado(generarCodigoEmpleado());
        return empleadoRepository.save(empleado);
    }
    
    public Empleado actualizarEmpleado(Integer id, Empleado empleado) {
        Empleado existente = obtenerEmpleadoPorId(id);
        existente.setNombre(empleado.getNombre());
        existente.setApellido(empleado.getApellido());
        existente.setDireccion(empleado.getDireccion());
        existente.setTelefono(empleado.getTelefono());
        existente.setCorreo(empleado.getCorreo());
        existente.setPuesto(empleado.getPuesto());
        existente.setDepartamento(empleado.getDepartamento());
        existente.setSalarioBase(empleado.getSalarioBase());
        return empleadoRepository.save(existente);
    }
    
    public void darBajaEmpleado(Integer id, LocalDate fechaTermino) {
        Empleado empleado = obtenerEmpleadoPorId(id);
        empleado.setEstado(Empleado.EstadoEmpleado.BAJA);
        empleado.setFechaTermino(fechaTermino);
        empleadoRepository.save(empleado);
    }
    
    private String generarCodigoEmpleado() {
        return "EMP-" + String.format("%05d", System.currentTimeMillis() % 100000);
    }
    
    @Transactional(readOnly = true)
    public Empleado obtenerEmpleadoPorId(Integer id) {
        return empleadoRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosActivos() {
        return empleadoRepository.findByEstadoWithRelations(Empleado.EstadoEmpleado.ACTIVO);
    }
    
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosPorDepartamento(Integer departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
        return empleadoRepository.findByDepartamentoWithRelations(departamento);
    }
}
