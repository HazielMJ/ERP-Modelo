package com.erp.erp.controller;

import com.erp.erp.dto.EmpleadoDTO;
import com.erp.erp.entity.*;
import com.erp.erp.mapper.EmpleadoMapper;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {
    private final EmpleadoService empleadoService;
    private final EmpleadoMapper empleadoMapper;
    
    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody Empleado empleado) {
        Empleado created = empleadoService.crearEmpleado(empleado);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(empleadoMapper.toDTO(created)); 
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(
        @PathVariable Integer id, 
        @RequestBody Empleado empleado
    ) {
        Empleado updated = empleadoService.actualizarEmpleado(id, empleado);
        return ResponseEntity.ok(empleadoMapper.toDTO(updated)); 
    }
    
    @PostMapping("/{id}/dar-baja")
    public ResponseEntity<Void> darBajaEmpleado(
        @PathVariable Integer id, 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTermino
    ) {
        empleadoService.darBajaEmpleado(id, fechaTermino);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Integer id) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorId(id);
        return ResponseEntity.ok(empleadoMapper.toDTO(empleado)); 
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleadosActivos() {
        List<Empleado> empleados = empleadoService.obtenerEmpleadosActivos();
        return ResponseEntity.ok(empleadoMapper.toDTOList(empleados)); 
    }
    
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleadosPorDepartamento(
        @PathVariable Integer departamentoId
    ) {
        List<Empleado> empleados = empleadoService.obtenerEmpleadosPorDepartamento(departamentoId);
        return ResponseEntity.ok(empleadoMapper.toDTOList(empleados));
    }
}
