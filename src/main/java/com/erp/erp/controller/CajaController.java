package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cajas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CajaController {
    
    private final CajaService cajaService;
    
    /**
     * Crear nueva caja
     * POST /api/cajas
     */
    @PostMapping
    public ResponseEntity<Caja> crearCaja(@Valid @RequestBody Caja caja) {
        log.info("POST /api/cajas - Creando nueva caja");
        Caja cajaNueva = cajaService.crearCaja(caja);
        return ResponseEntity.status(HttpStatus.CREATED).body(cajaNueva);
    }
    
    /**
     * Actualizar caja
     * PUT /api/cajas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Caja> actualizarCaja(
            @PathVariable Integer id,
            @Valid @RequestBody Caja caja) {
        log.info("PUT /api/cajas/{}", id);
        Caja cajaActualizada = cajaService.actualizarCaja(id, caja);
        return ResponseEntity.ok(cajaActualizada);
    }
    
    /**
     * Eliminar caja
     * DELETE /api/cajas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCaja(@PathVariable Integer id) {
        log.info("DELETE /api/cajas/{}", id);
        cajaService.eliminarCaja(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtener caja por ID
     * GET /api/cajas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Caja> obtenerCaja(@PathVariable Integer id) {
        log.info("GET /api/cajas/{}", id);
        Caja caja = cajaService.obtenerCajaPorId(id);
        return ResponseEntity.ok(caja);
    }
    
    /**
     * Obtener todas las cajas
     * GET /api/cajas
     */
    @GetMapping
    public ResponseEntity<List<Caja>> obtenerTodasLasCajas() {
        log.info("GET /api/cajas - Obteniendo todas las cajas");
        List<Caja> cajas = cajaService.obtenerTodasLasCajas();
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Obtener cajas activas
     * GET /api/cajas/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Caja>> obtenerCajasActivas() {
        log.info("GET /api/cajas/activas");
        List<Caja> cajas = cajaService.obtenerCajasActivas();
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Obtener cajas disponibles (activas sin apertura)
     * GET /api/cajas/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Caja>> obtenerCajasDisponibles() {
        log.info("GET /api/cajas/disponibles");
        List<Caja> cajas = cajaService.obtenerCajasDisponibles();
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Obtener cajas por estado
     * GET /api/cajas/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Caja>> obtenerCajasPorEstado(
            @PathVariable Caja.EstadoCaja estado) {
        log.info("GET /api/cajas/estado/{}", estado);
        List<Caja> cajas = cajaService.obtenerCajasPorEstado(estado);
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Obtener cajas con apertura abierta
     * GET /api/cajas/con-apertura-abierta
     */
    @GetMapping("/con-apertura-abierta")
    public ResponseEntity<List<Caja>> obtenerCajasConAperturaAbierta() {
        log.info("GET /api/cajas/con-apertura-abierta");
        List<Caja> cajas = cajaService.obtenerCajasConAperturaAbierta();
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Obtener cajas sin apertura hoy
     * GET /api/cajas/sin-apertura-hoy
     */
    @GetMapping("/sin-apertura-hoy")
    public ResponseEntity<List<Caja>> obtenerCajasSinAperturaHoy() {
        log.info("GET /api/cajas/sin-apertura-hoy");
        List<Caja> cajas = cajaService.obtenerCajasSinAperturaHoy();
        return ResponseEntity.ok(cajas);
    }
    
    /**
     * Cambiar estado de caja
     * PATCH /api/cajas/{id}/estado
     * Body: { "estado": "ACTIVA" }
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Caja> cambiarEstadoCaja(
            @PathVariable Integer id,
            @RequestBody Map<String, Caja.EstadoCaja> payload) {
        log.info("PATCH /api/cajas/{}/estado", id);
        
        Caja.EstadoCaja nuevoEstado = payload.get("estado");
        if (nuevoEstado == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Caja caja = cajaService.cambiarEstadoCaja(id, nuevoEstado);
        return ResponseEntity.ok(caja);
    }
    
    /**
     * Activar caja
     * POST /api/cajas/{id}/activar
     */
    @PostMapping("/{id}/activar")
    public ResponseEntity<Caja> activarCaja(@PathVariable Integer id) {
        log.info("POST /api/cajas/{}/activar", id);
        Caja caja = cajaService.activarCaja(id);
        return ResponseEntity.ok(caja);
    }
    
    /**
     * Desactivar caja
     * POST /api/cajas/{id}/desactivar
     */
    @PostMapping("/{id}/desactivar")
    public ResponseEntity<Caja> desactivarCaja(@PathVariable Integer id) {
        log.info("POST /api/cajas/{}/desactivar", id);
        Caja caja = cajaService.desactivarCaja(id);
        return ResponseEntity.ok(caja);
    }
    
    /**
     * Poner caja en mantenimiento
     * POST /api/cajas/{id}/mantenimiento
     */
    @PostMapping("/{id}/mantenimiento")
    public ResponseEntity<Caja> ponerCajaEnMantenimiento(@PathVariable Integer id) {
        log.info("POST /api/cajas/{}/mantenimiento", id);
        Caja caja = cajaService.ponerCajaEnMantenimiento(id);
        return ResponseEntity.ok(caja);
    }
    
    /**
     * Verificar si existe una caja por número
     * GET /api/cajas/existe/{numeroCaja}
     */
    @GetMapping("/existe/{numeroCaja}")
    public ResponseEntity<Map<String, Boolean>> existeCajaPorNumero(
            @PathVariable String numeroCaja) {
        log.info("GET /api/cajas/existe/{}", numeroCaja);
        boolean existe = cajaService.existeCajaPorNumero(numeroCaja);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
    
    /**
     * Contar cajas por estado
     * GET /api/cajas/contar/{estado}
     */
    @GetMapping("/contar/{estado}")
    public ResponseEntity<Map<String, Long>> contarCajasPorEstado(
            @PathVariable Caja.EstadoCaja estado) {
        log.info("GET /api/cajas/contar/{}", estado);
        Long cantidad = cajaService.contarCajasPorEstado(estado);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }
}