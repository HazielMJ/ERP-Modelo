package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j  // ✅ Para logging
@RestController
@RequestMapping("/api/contabilidad")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContabilidadController {
    
    private final ContabilidadRepository contabilidadRepository;
    private final UsuarioRepository usuarioRepository;
    
    // ✅ GET ALL - Con logging
    @GetMapping
    public ResponseEntity<List<Contabilidad>> getAllAsientos() {
        try {
            log.info("📥 Solicitando todos los asientos contables");
            List<Contabilidad> asientos = contabilidadRepository.findAll();
            log.info("✅ Encontrados {} asientos", asientos.size());
            
            // Debug: mostrar datos
            asientos.forEach(a -> log.debug("  - Asiento: {} | Estado: {}", a.getNumeroAsiento(), a.getEstado()));
            
            return ResponseEntity.ok(asientos);
        } catch (Exception e) {
            log.error("❌ Error al cargar asientos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Contabilidad> getAsientoById(@PathVariable Integer id) {
        log.info("📥 Buscando asiento con ID: {}", id);
        return contabilidadRepository.findById(id)
            .map(asiento -> {
                log.info("✅ Asiento encontrado: {}", asiento.getNumeroAsiento());
                return ResponseEntity.ok(asiento);
            })
            .orElseGet(() -> {
                log.warn("⚠️ Asiento con ID {} no encontrado", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Contabilidad> createAsiento(@RequestBody Contabilidad asiento) {
        try {
            log.info("💾 Creando nuevo asiento: {}", asiento.getNumeroAsiento());
            
            // Validar usuario
            if (asiento.getUsuario() == null || asiento.getUsuario().getId() == null) {
                log.info("⚠️ Sin usuario asignado, usando admin (ID=1)");
                Usuario usuario = usuarioRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));
                asiento.setUsuario(usuario);
            }
            
            Contabilidad saved = contabilidadRepository.save(asiento);
            log.info("✅ Asiento creado exitosamente con ID: {}", saved.getIdAsiento());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("❌ Error al crear asiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Contabilidad> updateAsiento(@PathVariable Integer id, @RequestBody Contabilidad asiento) {
        log.info("🔄 Actualizando asiento ID: {}", id);
        
        return contabilidadRepository.findById(id)
            .map(existing -> {
                existing.setFechaAsiento(asiento.getFechaAsiento());
                existing.setNumeroAsiento(asiento.getNumeroAsiento());
                existing.setDescripcion(asiento.getDescripcion());
                existing.setEstado(asiento.getEstado());
                existing.setPeriodoContable(asiento.getPeriodoContable());
                existing.setTotalDebe(asiento.getTotalDebe());
                existing.setTotalHaber(asiento.getTotalHaber());
                
                Contabilidad updated = contabilidadRepository.save(existing);
                log.info("✅ Asiento actualizado: {}", updated.getNumeroAsiento());
                
                return ResponseEntity.ok(updated);
            })
            .orElseGet(() -> {
                log.warn("⚠️ Asiento con ID {} no encontrado para actualizar", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsiento(@PathVariable Integer id) {
        log.info("🗑️ Eliminando asiento ID: {}", id);
        
        if (contabilidadRepository.existsById(id)) {
            contabilidadRepository.deleteById(id);
            log.info("✅ Asiento eliminado exitosamente");
            return ResponseEntity.noContent().build();
        }
        
        log.warn("⚠️ Asiento con ID {} no encontrado para eliminar", id);
        return ResponseEntity.notFound().build();
    }
}
