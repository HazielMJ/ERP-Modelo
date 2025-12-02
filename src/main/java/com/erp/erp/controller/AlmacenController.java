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
@RequestMapping("/api/almacenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlmacenController {
    private final AlmacenService almacenService;
    
    @PostMapping
    public ResponseEntity<Almacen> crearAlmacen(@Valid @RequestBody Almacen almacen) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(almacenService.crearAlmacen(almacen));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Almacen> actualizarAlmacen(@PathVariable Integer id, @RequestBody Almacen almacen) {
        return ResponseEntity.ok(almacenService.actualizarAlmacen(id, almacen));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Almacen> obtenerAlmacen(@PathVariable Integer id) {
        return ResponseEntity.ok(almacenService.obtenerAlmacenPorId(id));
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Almacen>> obtenerAlmacenesActivos() {
        return ResponseEntity.ok(almacenService.obtenerAlmacenesActivos());
    }
}
