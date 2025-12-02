package com.erp.erp.controller;

import com.erp.erp.entity.SeguimientoEnvio;
import com.erp.erp.service.SeguimientoEnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/seguimiento-envios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SeguimientoEnvioController {
    private final SeguimientoEnvioService seguimientoEnvioService;
    
    @PostMapping
    public ResponseEntity<SeguimientoEnvio> crearSeguimiento(@Valid @RequestBody SeguimientoEnvio seguimiento) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(seguimientoEnvioService.crearSeguimiento(seguimiento));
    }
    
    @PostMapping("/envio/{envioId}")
    public ResponseEntity<SeguimientoEnvio> crearSeguimientoParaEnvio(
            @PathVariable Integer envioId,
            @RequestParam SeguimientoEnvio.EstadoSeguimiento estado,
            @RequestParam String descripcion,
            @RequestParam(required = false) Integer usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(seguimientoEnvioService.crearSeguimientoParaEnvio(envioId, estado, descripcion, usuarioId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SeguimientoEnvio> actualizarSeguimiento(
            @PathVariable Integer id, 
            @Valid @RequestBody SeguimientoEnvio seguimiento) {
        return ResponseEntity.ok(seguimientoEnvioService.actualizarSeguimiento(id, seguimiento));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSeguimiento(@PathVariable Integer id) {
        seguimientoEnvioService.eliminarSeguimiento(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoEnvio> obtenerSeguimiento(@PathVariable Integer id) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientoPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<SeguimientoEnvio>> obtenerTodos() {
        return ResponseEntity.ok(seguimientoEnvioService.listarTodos());
    }
    
    @GetMapping("/envio/{envioId}")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimientosPorEnvio(@PathVariable Integer envioId) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientosPorEnvio(envioId));
    }
    
    @GetMapping("/envio/{envioId}/ultimo")
    public ResponseEntity<SeguimientoEnvio> obtenerUltimoSeguimiento(@PathVariable Integer envioId) {
        try {
            return ResponseEntity.ok(seguimientoEnvioService.obtenerUltimoSeguimiento(envioId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimientosPorEstado(
            @PathVariable SeguimientoEnvio.EstadoSeguimiento estado) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientosPorEstado(estado));
    }
    
    @GetMapping("/envio/{envioId}/estado/{estado}")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimientosPorEnvioYEstado(
            @PathVariable Integer envioId,
            @PathVariable SeguimientoEnvio.EstadoSeguimiento estado) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientosPorEnvioYEstado(envioId, estado));
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimientosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientosPorPeriodo(inicio, fin));
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimientosPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(seguimientoEnvioService.obtenerSeguimientosPorUsuario(usuarioId));
    }
}
