package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nomina")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NominaController {
    private final NominaRepository nominaRepository;
    private final EmpleadoRepository empleadoRepository;
    
    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Nomina>> getAllNominas() {
        return ResponseEntity.ok(nominaRepository.findAll());
    }
    
    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Nomina> getNominaById(@PathVariable Integer id) {
        return nominaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Nomina> createNomina(@RequestBody Nomina nomina) {
        try {
            // Validar que el empleado exista
            if (nomina.getEmpleado() != null && nomina.getEmpleado().getIdEmpleado() != null) {
                Empleado empleado = empleadoRepository.findById(nomina.getEmpleado().getIdEmpleado())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
                nomina.setEmpleado(empleado);
            }
            
            Nomina saved = nominaRepository.save(nomina);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Nomina> updateNomina(@PathVariable Integer id, @RequestBody Nomina nomina) {
        return nominaRepository.findById(id)
            .map(existing -> {
                existing.setPeriodoNomina(nomina.getPeriodoNomina());
                existing.setFechaInicio(nomina.getFechaInicio());
                existing.setFechaFin(nomina.getFechaFin());
                existing.setFechaPago(nomina.getFechaPago());
                existing.setDiasTrabajados(nomina.getDiasTrabajados());
                existing.setSalarioDiario(nomina.getSalarioDiario());
                existing.setTotalPercepciones(nomina.getTotalPercepciones());
                existing.setTotalDeducciones(nomina.getTotalDeducciones());
                existing.setTotalNeto(nomina.getTotalNeto());
                existing.setTipoNomina(nomina.getTipoNomina());
                existing.setEstado(nomina.getEstado());
                return ResponseEntity.ok(nominaRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNomina(@PathVariable Integer id) {
        if (nominaRepository.existsById(id)) {
            nominaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
