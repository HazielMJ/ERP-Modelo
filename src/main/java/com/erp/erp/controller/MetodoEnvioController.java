package com.erp.erp.controller;

import com.erp.erp.entity.MetodoEnvio;
import com.erp.erp.service.MetodoEnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/metodos-envio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetodoEnvioController {
    private final MetodoEnvioService metodoEnvioService;

    @GetMapping
    public ResponseEntity<List<MetodoEnvio>> listar() {
        return ResponseEntity.ok(metodoEnvioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoEnvio> buscarPorId(@PathVariable Integer id) {
        return metodoEnvioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MetodoEnvio>> buscarPorEstado(@PathVariable String estado) {
        try {
            MetodoEnvio.EstadoMetodo estadoEnum = MetodoEnvio.EstadoMetodo.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(metodoEnvioService.buscarPorEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MetodoEnvio>> obtenerActivos() {
        return ResponseEntity.ok(metodoEnvioService.obtenerMetodosActivos());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<MetodoEnvio>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(metodoEnvioService.buscarPorNombre(nombre));
    }

    @PostMapping
    public ResponseEntity<MetodoEnvio> crear(@Valid @RequestBody MetodoEnvio metodoEnvio) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(metodoEnvioService.guardar(metodoEnvio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoEnvio> actualizar(@PathVariable Integer id, @RequestBody MetodoEnvio metodoEnvio) {
        return metodoEnvioService.buscarPorId(id).map(m -> {
            m.setNombre(metodoEnvio.getNombre());
            m.setDescripcion(metodoEnvio.getDescripcion());
            m.setCostoBase(metodoEnvio.getCostoBase());
            m.setCostoPorKm(metodoEnvio.getCostoPorKm());
            m.setTiempoEstimado(metodoEnvio.getTiempoEstimado());
            m.setEstado(metodoEnvio.getEstado());
            return ResponseEntity.ok(metodoEnvioService.guardar(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (metodoEnvioService.buscarPorId(id).isPresent()) {
            metodoEnvioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
