package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import com.erp.erp.service.AperturaCajaService.ResumenAperturaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apertura-caja")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AperturaCajaController {
    
    private final AperturaCajaService aperturaCajaService;
    
    /**
     * Abrir caja
     * POST /api/apertura-caja
     * Body: { "cajaId": 1, "usuarioId": 1, "saldoInicial": 1000.00, "observaciones": "..." }
     */
    @PostMapping
    public ResponseEntity<AperturaCaja> abrirCaja(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/apertura-caja - Abriendo caja");
        
        Integer cajaId = (Integer) payload.get("cajaId");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        BigDecimal saldoInicial = new BigDecimal(payload.get("saldoInicial").toString());
        String observaciones = (String) payload.get("observaciones");
        
        AperturaCaja apertura = aperturaCajaService.abrirCaja(
            cajaId, usuarioId, saldoInicial, observaciones
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(apertura);
    }
    
    /**
     * Cerrar caja
     * POST /api/apertura-caja/{id}/cerrar
     * Body: { "saldoReal": 5000.00, "observaciones": "..." }
     */
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<AperturaCaja> cerrarCaja(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {
        log.info("POST /api/apertura-caja/{}/cerrar", id);
        
        BigDecimal saldoReal = new BigDecimal(payload.get("saldoReal").toString());
        String observaciones = (String) payload.get("observaciones");
        
        AperturaCaja apertura = aperturaCajaService.cerrarCaja(id, saldoReal, observaciones);
        return ResponseEntity.ok(apertura);
    }
    
    /**
     * Obtener apertura por ID
     * GET /api/apertura-caja/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AperturaCaja> obtenerApertura(@PathVariable Integer id) {
        log.info("GET /api/apertura-caja/{}", id);
        AperturaCaja apertura = aperturaCajaService.obtenerAperturaPorId(id);
        return ResponseEntity.ok(apertura);
    }
    
    /**
     * Obtener todas las aperturas
     * GET /api/apertura-caja
     */
    @GetMapping
    public ResponseEntity<List<AperturaCaja>> obtenerTodasLasAperturas() {
        log.info("GET /api/apertura-caja - Obteniendo todas las aperturas");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerTodasLasAperturas();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener apertura abierta de una caja
     * GET /api/apertura-caja/caja/{cajaId}/abierta
     */
    @GetMapping("/caja/{cajaId}/abierta")
    public ResponseEntity<AperturaCaja> obtenerCajaAbierta(@PathVariable Integer cajaId) {
        log.info("GET /api/apertura-caja/caja/{}/abierta", cajaId);
        return aperturaCajaService.obtenerCajaAbierta(cajaId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtener apertura abierta de un usuario
     * GET /api/apertura-caja/usuario/{usuarioId}/abierta
     */
    @GetMapping("/usuario/{usuarioId}/abierta")
    public ResponseEntity<AperturaCaja> obtenerAperturaAbiertaPorUsuario(
            @PathVariable Integer usuarioId) {
        log.info("GET /api/apertura-caja/usuario/{}/abierta", usuarioId);
        return aperturaCajaService.obtenerAperturaAbiertaPorUsuario(usuarioId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtener aperturas abiertas actualmente
     * GET /api/apertura-caja/abiertas
     */
    @GetMapping("/abiertas")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasAbiertas() {
        log.info("GET /api/apertura-caja/abiertas");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasAbiertas();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener aperturas del día
     * GET /api/apertura-caja/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasDelDia() {
        log.info("GET /api/apertura-caja/hoy");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasDelDia();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener aperturas por período
     * GET /api/apertura-caja/periodo?inicio=2025-01-01T00:00:00&fin=2025-01-31T23:59:59
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/apertura-caja/periodo - inicio: {}, fin: {}", inicio, fin);
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasPorPeriodo(inicio, fin);
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener aperturas con diferencias
     * GET /api/apertura-caja/con-diferencias
     */
    @GetMapping("/con-diferencias")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasConDiferencias() {
        log.info("GET /api/apertura-caja/con-diferencias");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasConDiferencias();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener aperturas con faltante
     * GET /api/apertura-caja/con-faltante
     */
    @GetMapping("/con-faltante")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasConFaltante() {
        log.info("GET /api/apertura-caja/con-faltante");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasConFaltante();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener aperturas con sobrante
     * GET /api/apertura-caja/con-sobrante
     */
    @GetMapping("/con-sobrante")
    public ResponseEntity<List<AperturaCaja>> obtenerAperturasConSobrante() {
        log.info("GET /api/apertura-caja/con-sobrante");
        List<AperturaCaja> aperturas = aperturaCajaService.obtenerAperturasConSobrante();
        return ResponseEntity.ok(aperturas);
    }
    
    /**
     * Obtener saldo actual de una apertura
     * GET /api/apertura-caja/{id}/saldo
     */
    @GetMapping("/{id}/saldo")
    public ResponseEntity<Map<String, BigDecimal>> obtenerSaldoActual(@PathVariable Integer id) {
        log.info("GET /api/apertura-caja/{}/saldo", id);
        BigDecimal saldo = aperturaCajaService.obtenerSaldoActual(id);
        return ResponseEntity.ok(Map.of("saldoActual", saldo));
    }
    
    /**
     * Obtener resumen completo de una apertura
     * GET /api/apertura-caja/{id}/resumen
     */
    @GetMapping("/{id}/resumen")
    public ResponseEntity<ResumenAperturaDTO> obtenerResumenApertura(@PathVariable Integer id) {
        log.info("GET /api/apertura-caja/{}/resumen", id);
        ResumenAperturaDTO resumen = aperturaCajaService.obtenerResumenApertura(id);
        return ResponseEntity.ok(resumen);
    }
    
    /**
     * Verificar si existe apertura abierta para una caja
     * GET /api/apertura-caja/caja/{cajaId}/existe-abierta
     */
    @GetMapping("/caja/{cajaId}/existe-abierta")
    public ResponseEntity<Map<String, Boolean>> existeAperturaAbierta(@PathVariable Integer cajaId) {
        log.info("GET /api/apertura-caja/caja/{}/existe-abierta", cajaId);
        boolean existe = aperturaCajaService.existeAperturaAbierta(cajaId);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
    
    /**
     * Verificar si un usuario tiene apertura abierta
     * GET /api/apertura-caja/usuario/{usuarioId}/tiene-abierta
     */
    @GetMapping("/usuario/{usuarioId}/tiene-abierta")
    public ResponseEntity<Map<String, Boolean>> usuarioTieneAperturaAbierta(
            @PathVariable Integer usuarioId) {
        log.info("GET /api/apertura-caja/usuario/{}/tiene-abierta", usuarioId);
        boolean tiene = aperturaCajaService.usuarioTieneAperturaAbierta(usuarioId);
        return ResponseEntity.ok(Map.of("tieneAbierta", tiene));
    }
}