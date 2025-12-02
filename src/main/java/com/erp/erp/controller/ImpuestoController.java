package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impuesto")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImpuestoController {
    private final ImpuestoRepository impuestoRepository;
    
    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Impuesto>> getAllImpuestos() {
        return ResponseEntity.ok(impuestoRepository.findAll());
    }
    
    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Impuesto> getImpuestoById(@PathVariable Integer id) {
        return impuestoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Impuesto> createImpuesto(@RequestBody Impuesto impuesto) {
        try {
            Impuesto saved = impuestoRepository.save(impuesto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Impuesto> updateImpuesto(@PathVariable Integer id, @RequestBody Impuesto impuesto) {
        return impuestoRepository.findById(id)
            .map(existing -> {
                existing.setNombre(impuesto.getNombre());
                existing.setTipo(impuesto.getTipo());
                existing.setTasa(impuesto.getTasa());
                existing.setDescripcion(impuesto.getDescripcion());
                existing.setEstado(impuesto.getEstado());
                return ResponseEntity.ok(impuestoRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImpuesto(@PathVariable Integer id) {
        if (impuestoRepository.existsById(id)) {
            impuestoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
