package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompraController {
    private final CompraService compraService;
    
    @PostMapping
    public ResponseEntity<Compra> crearCompra(@Valid @RequestBody Compra compra) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(compraService.crearCompra(compra));
    }
    
    @PostMapping("/{id}/detalles")
    public ResponseEntity<Compra> agregarDetalle(@PathVariable Integer id, @RequestBody DetalleCompra detalle) {
        return ResponseEntity.ok(compraService.agregarDetalleCompra(id, detalle));
    }
    
    @PostMapping("/{id}/recibir")
    public ResponseEntity<Void> recibirCompra(@PathVariable Integer id) {
        compraService.recibirCompra(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/recibir-parcial")
    public ResponseEntity<Void> recibirCompraParcial(
            @PathVariable Integer id,
            @RequestParam Integer detalleId,
            @RequestParam BigDecimal cantidadRecibida) {
        compraService.recibirCompraParcial(id, detalleId, cantidadRecibida);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/anular")
    public ResponseEntity<Void> anularCompra(@PathVariable Integer id) {
        compraService.anularCompra(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerCompra(@PathVariable Integer id) {
        return ResponseEntity.ok(compraService.obtenerCompraPorId(id));
    }
    
    // ⭐ NUEVO: Endpoint para listar TODAS las compras
    @GetMapping
    public ResponseEntity<List<Compra>> obtenerTodasLasCompras() {
        return ResponseEntity.ok(compraService.obtenerTodasLasCompras());
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<Compra>> obtenerComprasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(compraService.obtenerComprasPorPeriodo(inicio, fin));
    }
}
