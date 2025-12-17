package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnvioController {
    private final EnvioService envioService;
    
    // ========== OBTENER TODOS LOS ENVÍOS (CON RELACIONES) ==========
    @GetMapping
    public ResponseEntity<List<Envio>> obtenerTodos() {
        try {
            List<Envio> envios = envioService.obtenerTodos();
            return ResponseEntity.ok(envios);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== CREAR ENVÍO ==========
    @PostMapping
    public ResponseEntity<Envio> crearEnvio(@Valid @RequestBody Envio envio) {
        try {
            Envio nuevoEnvio = envioService.crearEnvio(envio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ========== CREAR ENVÍO DESDE VENTA ==========
    @PostMapping("/desde-venta/{ventaId}")
    public ResponseEntity<Envio> crearEnvioDesdeVenta(
            @PathVariable Integer ventaId, 
            @RequestBody Envio datosEnvio) {
        try {
            Envio envio = envioService.crearEnvioDesdeVenta(ventaId, datosEnvio);
            return ResponseEntity.status(HttpStatus.CREATED).body(envio);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ========== OBTENER ENVÍO POR ID ==========
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Integer id) {
        try {
            Envio envio = envioService.obtenerEnvioPorId(id);
            return ResponseEntity.ok(envio);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== ACTUALIZAR ENVÍO ==========
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizarEnvio(
            @PathVariable Integer id, 
            @RequestBody Envio envio) {
        try {
            Envio actualizado = envioService.actualizarEnvio(id, envio);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ========== OBTENER POR NÚMERO DE GUÍA ==========
    @GetMapping("/guia/{numeroGuia}")
    public ResponseEntity<Envio> obtenerEnvioPorGuia(@PathVariable String numeroGuia) {
        try {
            Envio envio = envioService.obtenerEnvioPorGuia(numeroGuia);
            return ResponseEntity.ok(envio);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== OBTENER ENVÍOS EN PROCESO ==========
    @GetMapping("/en-proceso")
    public ResponseEntity<List<Envio>> obtenerEnviosEnProceso() {
        try {
            List<Envio> envios = envioService.obtenerEnviosEnProceso();
            return ResponseEntity.ok(envios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== ACTUALIZAR ESTADO ==========
    @PostMapping("/{id}/actualizar-estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> datos) {
        try {
            String estadoStr = datos.get("estado");
            if (estadoStr == null || estadoStr.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El estado no puede estar vacío");
            }
            
            Envio.EstadoEnvio estado;
            try {
                estado = Envio.EstadoEnvio.valueOf(estadoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estado inválido: " + estadoStr + ". Estados válidos: PENDIENTE, PREPARANDO, EN_TRANSITO, ENTREGADO, CANCELADO, DEVUELTO");
            }
            
            envioService.actualizarEstadoEnvio(id, estado, datos.get("descripcion"));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el estado: " + e.getMessage());
        }
    }
    
    // ========== ASIGNAR TRANSPORTISTA ==========
    @PostMapping("/{envioId}/asignar-transportista/{transportistaId}")
    public ResponseEntity<Void> asignarTransportista(
            @PathVariable Integer envioId, 
            @PathVariable Integer transportistaId) {
        try {
            envioService.asignarTransportista(envioId, transportistaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ========== CREAR SEGUIMIENTO ==========
    @PostMapping("/{id}/seguimiento")
    public ResponseEntity<SeguimientoEnvio> crearSeguimiento(
            @PathVariable Integer id,
            @RequestBody Map<String, String> datos) {
        try {
            SeguimientoEnvio.EstadoSeguimiento estado = 
                SeguimientoEnvio.EstadoSeguimiento.valueOf(datos.get("estado"));
            SeguimientoEnvio seguimiento = envioService.crearSeguimiento(id, datos.get("descripcion"), estado);
            return ResponseEntity.status(HttpStatus.CREATED).body(seguimiento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // ========== OBTENER SEGUIMIENTO ==========
    @GetMapping("/{id}/seguimiento")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimiento(@PathVariable Integer id) {
        try {
            List<SeguimientoEnvio> seguimientos = envioService.obtenerSeguimientoEnvio(id);
            return ResponseEntity.ok(seguimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== ELIMINAR ENVÍO ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Integer id) {
        try {
            envioService.eliminarEnvio(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
