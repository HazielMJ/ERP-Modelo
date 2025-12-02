package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    
    public Proveedor crearProveedor(Proveedor proveedor) {
        if (proveedor.getRfc() != null && proveedorRepository.findByRfc(proveedor.getRfc()).isPresent()) {
            throw new RuntimeException("Ya existe un proveedor con ese RFC");
        }
        return proveedorRepository.save(proveedor);
    }
    
    public Proveedor actualizarProveedor(Integer id, Proveedor proveedor) {
        Proveedor existente = obtenerProveedorPorId(id);
        existente.setNombreEmpresa(proveedor.getNombreEmpresa());
        existente.setRfc(proveedor.getRfc());
        existente.setDireccion(proveedor.getDireccion());
        existente.setTelefono(proveedor.getTelefono());
        existente.setEmail(proveedor.getEmail());
        existente.setContactoNombre(proveedor.getContactoNombre());
        existente.setContactoTelefono(proveedor.getContactoTelefono());
        existente.setLimiteCredito(proveedor.getLimiteCredito());
        existente.setObservaciones(proveedor.getObservaciones());
        return proveedorRepository.save(existente);
    }
    
    public void actualizarSaldo(Integer id, BigDecimal monto, boolean esAumento) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        BigDecimal nuevoSaldo = esAumento ? 
            proveedor.getSaldoActual().add(monto) : 
            proveedor.getSaldoActual().subtract(monto);
        proveedor.setSaldoActual(nuevoSaldo);
        proveedorRepository.save(proveedor);
    }
    
    public void desactivarProveedor(Integer id) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        proveedor.setEstado(Proveedor.EstadoProveedor.INACTIVO);
        proveedorRepository.save(proveedor);
    }
    
    @Transactional(readOnly = true)
    public Proveedor obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Proveedor> obtenerProveedoresActivos() {
        return proveedorRepository.findByEstado(Proveedor.EstadoProveedor.ACTIVO);
    }
    
    @Transactional(readOnly = true)
    public List<Proveedor> buscarProveedoresPorNombre(String nombre) {
        return proveedorRepository.findByNombreEmpresaContaining(nombre);
    }
}
