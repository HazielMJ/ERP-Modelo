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
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RutaController {
    private final RutaService rutaService;
    
    @PostMapping
    public ResponseEntity<Ruta> crearRuta(@Valid @RequestBody Ruta ruta) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(rutaService.crearRuta(ruta));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizarRuta(@PathVariable Integer id, @RequestBody Ruta ruta) {
        return ResponseEntity.ok(rutaService.actualizarRuta(id, ruta));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerRuta(@PathVariable Integer id) {
        return ResponseEntity.ok(rutaService.obtenerRutaPorId(id));
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Ruta>> obtenerRutasActivas() {
        return ResponseEntity.ok(rutaService.obtenerRutasActivas());
    }
}
