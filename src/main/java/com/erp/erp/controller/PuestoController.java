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
@RequestMapping("/api/puestos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PuestoController {
    private final PuestoService puestoService;
    
    @PostMapping
    public ResponseEntity<Puesto> crearPuesto(@Valid @RequestBody Puesto puesto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(puestoService.crearPuesto(puesto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Puesto> actualizarPuesto(@PathVariable Integer id, @RequestBody Puesto puesto) {
        return ResponseEntity.ok(puestoService.actualizarPuesto(id, puesto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Puesto> obtenerPuesto(@PathVariable Integer id) {
        return ResponseEntity.ok(puestoService.obtenerPuestoPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Puesto>> obtenerTodosLosPuestos() {
        return ResponseEntity.ok(puestoService.obtenerTodosLosPuestos());
    }
}
