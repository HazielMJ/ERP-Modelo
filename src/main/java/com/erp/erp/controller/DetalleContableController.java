package com.erp.erp.controller;

import com.erp.erp.entity.DetalleContable;
import com.erp.erp.repository.DetalleContableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/detallecontable")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DetalleContableController {
    
    private final DetalleContableRepository detalleContableRepository;
    
    @GetMapping
    public ResponseEntity<List<DetalleContable>> getAll() {
        try {
            log.info("📥 Solicitando todos los detalles contables");
            List<DetalleContable> detalles = detalleContableRepository.findAll();
            log.info("✅ Encontrados {} detalles", detalles.size());
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalleContable> getById(@PathVariable Integer id) {
        log.info("📥 Buscando detalle ID: {}", id);
        return detalleContableRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<DetalleContable> create(@RequestBody DetalleContable detalle) {
        try {
            log.info("💾 Creando detalle contable");
            DetalleContable saved = detalleContableRepository.save(detalle);
            log.info("✅ Detalle creado con ID: {}", saved.getIdDetalle());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("🗑️ Eliminando detalle ID: {}", id);
        if (detalleContableRepository.existsById(id)) {
            detalleContableRepository.deleteById(id);
            log.info("✅ Detalle eliminado");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
