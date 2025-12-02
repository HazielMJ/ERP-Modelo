package com.erp.erp.controller;

import com.erp.erp.entity.DetalleNomina;
import com.erp.erp.repository.DetalleNominaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/detallenomina")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DetalleNominaController {
    
    private final DetalleNominaRepository detalleNominaRepository;
    
    @GetMapping
    public ResponseEntity<List<DetalleNomina>> getAll() {
        try {
            log.info("📥 Solicitando todos los detalles de nómina");
            List<DetalleNomina> detalles = detalleNominaRepository.findAll();
            log.info("✅ Encontrados {} detalles", detalles.size());
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DetalleNomina> getById(@PathVariable Integer id) {
        log.info("📥 Buscando detalle ID: {}", id);
        return detalleNominaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<DetalleNomina> create(@RequestBody DetalleNomina detalle) {
        try {
            log.info("💾 Creando detalle de nómina");
            DetalleNomina saved = detalleNominaRepository.save(detalle);
            log.info("✅ Detalle creado con ID: {}", saved.getIdDetalleNomina());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("🗑️ Eliminando detalle ID: {}", id);
        if (detalleNominaRepository.existsById(id)) {
            detalleNominaRepository.deleteById(id);
            log.info("✅ Detalle eliminado");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
