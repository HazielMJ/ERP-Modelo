package com.erp.erp.controller;

import com.erp.erp.dto.VentaDTO;
import com.erp.erp.entity.Venta;
import com.erp.erp.service.VentaService;
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
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class VentaController {
    
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@Valid @RequestBody VentaDTO ventaDTO) {
        log.info("POST /api/ventas - Creando nueva venta");
        VentaDTO ventaCreada = ventaService.crearVenta(ventaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaCreada);
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<VentaDTO> agregarDetalle(
            @PathVariable Integer id, 
            @Valid @RequestBody VentaDTO.DetalleVentaDTO detalleDTO) {
        log.info("POST /api/ventas/{}/detalles - Agregando detalle", id);
        VentaDTO ventaActualizada = ventaService.agregarDetalleVenta(id, detalleDTO);
        return ResponseEntity.ok(ventaActualizada);
    }
    

    @PostMapping("/{id}/procesar-pago")
    public ResponseEntity<VentaDTO> procesarPago(
            @PathVariable Integer id, 
            @RequestBody Map<String, BigDecimal> payload) {
        log.info("POST /api/ventas/{}/procesar-pago", id);
        
        BigDecimal montoRecibido = payload.get("montoRecibido");
        if (montoRecibido == null) {
            return ResponseEntity.badRequest().build();
        }
        
        VentaDTO ventaPagada = ventaService.procesarPagoVenta(id, montoRecibido);
        return ResponseEntity.ok(ventaPagada);
    }
    
    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anularVenta(@PathVariable Integer id) {
        log.info("POST /api/ventas/{}/anular", id);
        ventaService.anularVenta(id);
        return ResponseEntity.ok().build();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerVenta(@PathVariable Integer id) {
        log.info("GET /api/ventas/{}", id);
        VentaDTO venta = ventaService.obtenerVentaPorId(id);
        return ResponseEntity.ok(venta);
    }
    
    @GetMapping
    public ResponseEntity<List<VentaDTO>> obtenerTodasLasVentas() {
        log.info("GET /api/ventas - Obteniendo todas las ventas");
        List<VentaDTO> ventas = ventaService.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
    

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VentaDTO>> obtenerVentasPorEstado(
            @PathVariable Venta.EstadoVenta estado) {
        log.info("GET /api/ventas/estado/{}", estado);
        List<VentaDTO> ventas = ventaService.obtenerVentasPorEstado(estado);
        return ResponseEntity.ok(ventas);
    }
    

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaDTO>> obtenerVentasPorCliente(
            @PathVariable Integer clienteId) {
        log.info("GET /api/ventas/cliente/{}", clienteId);
        List<VentaDTO> ventas = ventaService.obtenerVentasPorCliente(clienteId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VentaDTO>> obtenerVentasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/ventas/periodo - inicio: {}, fin: {}", inicio, fin);
        List<VentaDTO> ventas = ventaService.obtenerVentasPorPeriodo(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/total-periodo")
    public ResponseEntity<Map<String, BigDecimal>> obtenerTotalVentasPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/ventas/total-periodo - inicio: {}, fin: {}", inicio, fin);
        BigDecimal total = ventaService.obtenerTotalVentasPorPeriodo(inicio, fin);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<VentaDTO>> obtenerVentasDelDia() {
        log.info("GET /api/ventas/hoy - Obteniendo ventas del día");
        List<VentaDTO> ventas = ventaService.obtenerVentasDelDia();
        return ResponseEntity.ok(ventas);
    }
}