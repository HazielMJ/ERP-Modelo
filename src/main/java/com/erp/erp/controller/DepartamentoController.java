package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepartamentoController {
    private final DepartamentoService departamentoService;
    
    @PostMapping
    public ResponseEntity<Departamento> crearDepartamento(@Valid @RequestBody Departamento departamento) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(departamentoService.crearDepartamento(departamento));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizarDepartamento(@PathVariable Integer id, @RequestBody Departamento departamento) {
        return ResponseEntity.ok(departamentoService.actualizarDepartamento(id, departamento));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtenerDepartamento(@PathVariable Integer id) {
        return ResponseEntity.ok(departamentoService.obtenerDepartamentoPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Departamento>> obtenerTodosLosDepartamentos() {
        return ResponseEntity.ok(departamentoService.obtenerTodosLosDepartamentos());
    }
}

