package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimiento-caja")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MovimientoCajaController {
    
    private final MovimientoCajaService movimientoCajaService;
    
    /**
     * Registrar movimiento genérico
     * POST /api/movimiento-caja
     */
    @PostMapping
    public ResponseEntity<MovimientoCaja> registrarMovimiento(
            @RequestParam Integer aperturaId,
            @Valid @RequestBody MovimientoCaja movimiento) {
        log.info("POST /api/movimiento-caja - Apertura: {}", aperturaId);
        MovimientoCaja movimientoGuardado = movimientoCajaService
            .registrarMovimiento(aperturaId, movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoGuardado);
    }
    
    /**
     * Registrar venta en caja
     * POST /api/movimiento-caja/venta
     * Body: { "aperturaId": 1, "ventaId": 1, "monto": 1000.00, "formaPago": "EFECTIVO", "usuarioId": 1 }
     */
    @PostMapping("/venta")
    public ResponseEntity<MovimientoCaja> registrarVenta(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/movimiento-caja/venta");
        
        Integer aperturaId = (Integer) payload.get("aperturaId");
        Integer ventaId = (Integer) payload.get("ventaId");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String formaPagoStr = (String) payload.get("formaPago");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        
        MovimientoCaja.FormaPago formaPago = MovimientoCaja.FormaPago.valueOf(formaPagoStr);
        
        MovimientoCaja movimiento = movimientoCajaService.registrarVenta(
            aperturaId, ventaId, monto, formaPago, usuarioId
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
    
    /**
     * Registrar retiro de efectivo
     * POST /api/movimiento-caja/retiro
     * Body: { "aperturaId": 1, "monto": 500.00, "concepto": "...", "usuarioId": 1, "referencia": "..." }
     */
    @PostMapping("/retiro")
    public ResponseEntity<MovimientoCaja> registrarRetiro(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/movimiento-caja/retiro");
        
        Integer aperturaId = (Integer) payload.get("aperturaId");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String concepto = (String) payload.get("concepto");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        String referencia = (String) payload.get("referencia");
        
        MovimientoCaja movimiento = movimientoCajaService.registrarRetiro(
            aperturaId, monto, concepto, usuarioId, referencia
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
    
    /**
     * Registrar gasto
     * POST /api/movimiento-caja/gasto
     * Body: { "aperturaId": 1, "monto": 200.00, "concepto": "...", "usuarioId": 1, "referencia": "..." }
     */
    @PostMapping("/gasto")
    public ResponseEntity<MovimientoCaja> registrarGasto(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/movimiento-caja/gasto");
        
        Integer aperturaId = (Integer) payload.get("aperturaId");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String concepto = (String) payload.get("concepto");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        String referencia = (String) payload.get("referencia");
        
        MovimientoCaja movimiento = movimientoCajaService.registrarGasto(
            aperturaId, monto, concepto, usuarioId, referencia
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
    
    /**
     * Registrar depósito
     * POST /api/movimiento-caja/deposito
     * Body: { "aperturaId": 1, "monto": 1000.00, "concepto": "...", "usuarioId": 1, "referencia": "..." }
     */
    @PostMapping("/deposito")
    public ResponseEntity<MovimientoCaja> registrarDeposito(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/movimiento-caja/deposito");
        
        Integer aperturaId = (Integer) payload.get("aperturaId");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String concepto = (String) payload.get("concepto");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        String referencia = (String) payload.get("referencia");
        
        MovimientoCaja movimiento = movimientoCajaService.registrarDeposito(
            aperturaId, monto, concepto, usuarioId, referencia
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
    
    /**
     * Registrar ajuste
     * POST /api/movimiento-caja/ajuste
     * Body: { "aperturaId": 1, "monto": 50.00, "tipo": "INGRESO", "concepto": "...", "usuarioId": 1, "referencia": "..." }
     */
    @PostMapping("/ajuste")
    public ResponseEntity<MovimientoCaja> registrarAjuste(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/movimiento-caja/ajuste");
        
        Integer aperturaId = (Integer) payload.get("aperturaId");
        BigDecimal monto = new BigDecimal(payload.get("monto").toString());
        String tipoStr = (String) payload.get("tipo");
        String concepto = (String) payload.get("concepto");
        Integer usuarioId = (Integer) payload.get("usuarioId");
        String referencia = (String) payload.get("referencia");
        
        MovimientoCaja.TipoMovimiento tipo = MovimientoCaja.TipoMovimiento.valueOf(tipoStr);
        
        MovimientoCaja movimiento = movimientoCajaService.registrarAjuste(
            aperturaId, monto, tipo, concepto, usuarioId, referencia
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
    
    /**
     * Eliminar movimiento
     * DELETE /api/movimiento-caja/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Integer id) {
        log.info("DELETE /api/movimiento-caja/{}", id);
        movimientoCajaService.eliminarMovimiento(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtener movimiento por ID
     * GET /api/movimiento-caja/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoCaja> obtenerMovimiento(@PathVariable Integer id) {
        log.info("GET /api/movimiento-caja/{}", id);
        MovimientoCaja movimiento = movimientoCajaService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(movimiento);
    }
    
    /**
     * Obtener movimientos de una apertura
     * GET /api/movimiento-caja/apertura/{aperturaId}
     */
    @GetMapping("/apertura/{aperturaId}")
    public ResponseEntity<List<MovimientoCaja>> obtenerMovimientosPorApertura(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}", aperturaId);
        List<MovimientoCaja> movimientos = movimientoCajaService
            .obtenerMovimientosPorApertura(aperturaId);
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener ventas de una apertura
     * GET /api/movimiento-caja/apertura/{aperturaId}/ventas
     */
    @GetMapping("/apertura/{aperturaId}/ventas")
    public ResponseEntity<List<MovimientoCaja>> obtenerVentasPorApertura(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}/ventas", aperturaId);
        List<MovimientoCaja> ventas = movimientoCajaService.obtenerVentasPorApertura(aperturaId);
        return ResponseEntity.ok(ventas);
    }
    
    /**
     * Obtener retiros de una apertura
     * GET /api/movimiento-caja/apertura/{aperturaId}/retiros
     */
    @GetMapping("/apertura/{aperturaId}/retiros")
    public ResponseEntity<List<MovimientoCaja>> obtenerRetirosPorApertura(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}/retiros", aperturaId);
        List<MovimientoCaja> retiros = movimientoCajaService.obtenerRetirosPorApertura(aperturaId);
        return ResponseEntity.ok(retiros);
    }
    
    /**
     * Obtener gastos de una apertura
     * GET /api/movimiento-caja/apertura/{aperturaId}/gastos
     */
    @GetMapping("/apertura/{aperturaId}/gastos")
    public ResponseEntity<List<MovimientoCaja>> obtenerGastosPorApertura(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}/gastos", aperturaId);
        List<MovimientoCaja> gastos = movimientoCajaService.obtenerGastosPorApertura(aperturaId);
        return ResponseEntity.ok(gastos);
    }
    
    /**
     * Obtener movimientos del día
     * GET /api/movimiento-caja/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<MovimientoCaja>> obtenerMovimientosDelDia() {
        log.info("GET /api/movimiento-caja/hoy");
        List<MovimientoCaja> movimientos = movimientoCajaService.obtenerMovimientosDelDia();
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener movimientos por período
     * GET /api/movimiento-caja/periodo?inicio=2025-01-01T00:00:00&fin=2025-01-31T23:59:59
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<MovimientoCaja>> obtenerMovimientosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/movimiento-caja/periodo - inicio: {}, fin: {}", inicio, fin);
        List<MovimientoCaja> movimientos = movimientoCajaService
            .obtenerMovimientosPorPeriodo(inicio, fin);
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener total por tipo de movimiento
     * GET /api/movimiento-caja/apertura/{aperturaId}/total/{tipo}
     */
    @GetMapping("/apertura/{aperturaId}/total/{tipo}")
    public ResponseEntity<Map<String, BigDecimal>> obtenerTotalPorTipo(
            @PathVariable Integer aperturaId,
            @PathVariable MovimientoCaja.TipoMovimiento tipo) {
        log.info("GET /api/movimiento-caja/apertura/{}/total/{}", aperturaId, tipo);
        BigDecimal total = movimientoCajaService.obtenerTotalPorTipo(aperturaId, tipo);
        return ResponseEntity.ok(Map.of("total", total));
    }
    
    /**
     * Obtener totales por forma de pago
     * GET /api/movimiento-caja/apertura/{aperturaId}/totales-forma-pago
     */
    @GetMapping("/apertura/{aperturaId}/totales-forma-pago")
    public ResponseEntity<List<Object[]>> obtenerTotalesPorFormaPago(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}/totales-forma-pago", aperturaId);
        List<Object[]> totales = movimientoCajaService.obtenerTotalesPorFormaPago(aperturaId);
        return ResponseEntity.ok(totales);
    }
    
    /**
     * Obtener estadísticas por categoría
     * GET /api/movimiento-caja/apertura/{aperturaId}/estadisticas-categoria
     */
    @GetMapping("/apertura/{aperturaId}/estadisticas-categoria")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorCategoria(
            @PathVariable Integer aperturaId) {
        log.info("GET /api/movimiento-caja/apertura/{}/estadisticas-categoria", aperturaId);
        List<Object[]> estadisticas = movimientoCajaService
            .obtenerEstadisticasPorCategoria(aperturaId);
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Verificar si existe movimiento para una venta
     * GET /api/movimiento-caja/venta/{ventaId}/existe
     */
    @GetMapping("/venta/{ventaId}/existe")
    public ResponseEntity<Map<String, Boolean>> existeMovimientoParaVenta(
            @PathVariable Integer ventaId) {
        log.info("GET /api/movimiento-caja/venta/{}/existe", ventaId);
        boolean existe = movimientoCajaService.existeMovimientoParaVenta(ventaId);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
}