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
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransportistaController {
    private final TransportistaService transportistaService;
    
    @PostMapping
    public ResponseEntity<Transportista> crearTransportista(@Valid @RequestBody Transportista transportista) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(transportistaService.crearTransportista(transportista));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Transportista> actualizarTransportista(@PathVariable Integer id, @RequestBody Transportista transportista) {
        return ResponseEntity.ok(transportistaService.actualizarTransportista(id, transportista));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerTransportista(@PathVariable Integer id) {
        return ResponseEntity.ok(transportistaService.obtenerTransportistaPorId(id));
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Transportista>> obtenerTransportistasActivos() {
        return ResponseEntity.ok(transportistaService.obtenerTransportistasActivos());
    }
}
