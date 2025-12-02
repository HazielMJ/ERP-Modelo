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
    
    @PostMapping
    public ResponseEntity<Envio> crearEnvio(@Valid @RequestBody Envio envio) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(envioService.crearEnvio(envio));
    }
    
    @PostMapping("/desde-venta/{ventaId}")
    public ResponseEntity<Envio> crearEnvioDesdeVenta(@PathVariable Integer ventaId, @RequestBody Envio datosEnvio) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(envioService.crearEnvioDesdeVenta(ventaId, datosEnvio));
    }
    
    @PostMapping("/{id}/seguimiento")
    public ResponseEntity<SeguimientoEnvio> crearSeguimiento(
            @PathVariable Integer id,
            @RequestBody Map<String, String> datos) {
        SeguimientoEnvio.EstadoSeguimiento estado = 
            SeguimientoEnvio.EstadoSeguimiento.valueOf(datos.get("estado"));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(envioService.crearSeguimiento(id, datos.get("descripcion"), estado));
    }
    
    @PostMapping("/{id}/actualizar-estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> datos) {
        Envio.EstadoEnvio estado = Envio.EstadoEnvio.valueOf(datos.get("estado"));
        envioService.actualizarEstadoEnvio(id, estado, datos.get("descripcion"));
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{envioId}/asignar-transportista/{transportistaId}")
    public ResponseEntity<Void> asignarTransportista(@PathVariable Integer envioId, @PathVariable Integer transportistaId) {
        envioService.asignarTransportista(envioId, transportistaId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Integer id) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorId(id));
    }
    
    @GetMapping("/guia/{numeroGuia}")
    public ResponseEntity<Envio> obtenerEnvioPorGuia(@PathVariable String numeroGuia) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorGuia(numeroGuia));
    }
    
    @GetMapping("/en-proceso")
    public ResponseEntity<List<Envio>> obtenerEnviosEnProceso() {
        return ResponseEntity.ok(envioService.obtenerEnviosEnProceso());
    }
    
    @GetMapping("/{id}/seguimiento")
    public ResponseEntity<List<SeguimientoEnvio>> obtenerSeguimiento(@PathVariable Integer id) {
        return ResponseEntity.ok(envioService.obtenerSeguimientoEnvio(id));
    }
}
