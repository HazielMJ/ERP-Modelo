package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factura")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FacturaController {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    
    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Factura>> getAllFacturas() {
        return ResponseEntity.ok(facturaRepository.findAll());
    }
    
    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFacturaById(@PathVariable Integer id) {
        return facturaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Factura> createFactura(@RequestBody Factura factura) {
        try {
            // Validar cliente
            if (factura.getCliente() != null && factura.getCliente().getIdCliente() != null) {
                Cliente cliente = clienteRepository.findById(factura.getCliente().getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                factura.setCliente(cliente);
            }
            
            // Asignar usuario admin si no viene
            if (factura.getUsuario() == null) {
                Usuario usuario = usuarioRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                factura.setUsuario(usuario);
            }
            
            Factura saved = facturaRepository.save(factura);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Factura> updateFactura(@PathVariable Integer id, @RequestBody Factura factura) {
        return facturaRepository.findById(id)
            .map(existing -> {
                existing.setEstado(factura.getEstado());
                existing.setFormaPago(factura.getFormaPago());
                existing.setUsoCfdi(factura.getUsoCfdi());
                existing.setMetodoPago(factura.getMetodoPago());
                existing.setUuidFiscal(factura.getUuidFiscal());
                existing.setSubtotal(factura.getSubtotal());
                existing.setImpuestos(factura.getImpuestos());
                existing.setDescuentos(factura.getDescuentos());
                existing.setTotal(factura.getTotal());
                return ResponseEntity.ok(facturaRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Integer id) {
        if (facturaRepository.existsById(id)) {
            facturaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
