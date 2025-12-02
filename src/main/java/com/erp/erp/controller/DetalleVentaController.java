package com.erp.erp.controller;

import com.erp.erp.entity.DetalleVenta;
import com.erp.erp.service.DetalleVentaService;
import com.erp.erp.service.DetalleVentaService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detalles-venta")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DetalleVentaController {
    
    private final DetalleVentaService detalleVentaService;
    

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DetalleVenta>> obtenerDetallesPorVenta(
            @PathVariable Integer ventaId) {
        log.info("GET /api/detalles-venta/venta/{}", ventaId);
        List<DetalleVenta> detalles = detalleVentaService.obtenerDetallesPorVenta(ventaId);
        return ResponseEntity.ok(detalles);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetalle(@PathVariable Integer id) {
        log.info("GET /api/detalles-venta/{}", id);
        DetalleVenta detalle = detalleVentaService.obtenerDetallePorId(id);
        return ResponseEntity.ok(detalle);
    }
    

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoVendidoDTO>> obtenerProductosMasVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/detalles-venta/productos-mas-vendidos");
        List<ProductoVendidoDTO> productos = detalleVentaService
            .obtenerProductosMasVendidos(inicio, fin);
        return ResponseEntity.ok(productos);
    }
    

    @GetMapping("/estadisticas-producto/{productoId}")
    public ResponseEntity<EstadisticasProductoDTO> obtenerEstadisticasProducto(
            @PathVariable Integer productoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/detalles-venta/estadisticas-producto/{}", productoId);
        EstadisticasProductoDTO estadisticas = detalleVentaService
            .obtenerEstadisticasProducto(productoId, inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/total-unidades")
    public ResponseEntity<Map<String, BigDecimal>> obtenerTotalUnidades(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/detalles-venta/total-unidades");
        BigDecimal total = detalleVentaService.obtenerTotalUnidadesVendidas(inicio, fin);
        return ResponseEntity.ok(Map.of("totalUnidades", total));
    }
    

    @GetMapping("/total-descuentos")
    public ResponseEntity<Map<String, BigDecimal>> obtenerTotalDescuentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/detalles-venta/total-descuentos");
        BigDecimal total = detalleVentaService.obtenerTotalDescuentos(inicio, fin);
        return ResponseEntity.ok(Map.of("totalDescuentos", total));
    }
    

    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<List<VentasPorCategoriaDTO>> obtenerVentasPorCategoria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/detalles-venta/ventas-por-categoria");
        List<VentasPorCategoriaDTO> ventas = detalleVentaService
            .obtenerVentasPorCategoria(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<DetalleVenta>> obtenerDetallesVentasHoy() {
        log.info("GET /api/detalles-venta/hoy");
        List<DetalleVenta> detalles = detalleVentaService.obtenerDetallesVentasHoy();
        return ResponseEntity.ok(detalles);
    }
}