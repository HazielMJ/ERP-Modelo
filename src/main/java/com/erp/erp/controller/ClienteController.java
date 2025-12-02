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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {
    private final ClienteService clienteService;
    
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(clienteService.crearCliente(cliente));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, cliente));
    }
    
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarCliente(@PathVariable Integer id) {
        clienteService.desactivarCliente(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        return ResponseEntity.ok(clienteService.obtenerTodosLosClientes());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Cliente>> obtenerClientesActivos() {
        return ResponseEntity.ok(clienteService.obtenerClientesActivos());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarClientes(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteService.buscarClientesPorNombre(nombre));
    }
    
    @GetMapping("/saldo-excedido")
    public ResponseEntity<List<Cliente>> obtenerClientesConSaldoExcedido() {
        return ResponseEntity.ok(clienteService.obtenerClientesConSaldoExcedido());
    }
}
