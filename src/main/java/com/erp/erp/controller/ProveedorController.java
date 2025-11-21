package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {
    private final ProveedorService proveedorService;
    
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@Valid @RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(proveedorService.crearProveedor(proveedor));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Integer id, @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, proveedor));
    }
    
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarProveedor(@PathVariable Integer id) {
        proveedorService.desactivarProveedor(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.obtenerProveedorPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodosLosProveedores() {
        return ResponseEntity.ok(proveedorService.obtenerTodosLosProveedores());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Proveedor>> obtenerProveedoresActivos() {
        return ResponseEntity.ok(proveedorService.obtenerProveedoresActivos());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Proveedor>> buscarProveedores(@RequestParam String nombre) {
        return ResponseEntity.ok(proveedorService.buscarProveedoresPorNombre(nombre));
    }
}
