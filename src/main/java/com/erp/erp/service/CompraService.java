package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraService {
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorService proveedorService;
    private final InventarioService inventarioService;
    
    public Compra crearCompra(Compra compra) {
        compra.setNumeroCompra(generarNumeroCompra());
        calcularTotalesCompra(compra);
        return compraRepository.save(compra);
    }
    
    public Compra agregarDetalleCompra(Integer compraId, DetalleCompra detalle) {
        Compra compra = obtenerCompraPorId(compraId);
        if (compra.getEstado() != Compra.EstadoCompra.PENDIENTE) {
            throw new RuntimeException("No se pueden agregar productos a una compra " + compra.getEstado());
        }
        
        detalle.setCompra(compra);
        calcularTotalesDetalle(detalle);
        detalleCompraRepository.save(detalle);
        
        compra.getDetalles().add(detalle);
        calcularTotalesCompra(compra);
        return compraRepository.save(compra);
    }
    
    public void recibirCompra(Integer id) {
        Compra compra = obtenerCompraPorId(id);
        if (compra.getEstado() == Compra.EstadoCompra.RECIBIDA) {
            throw new RuntimeException("La compra ya fue recibida");
        }
        
        for (DetalleCompra detalle : compra.getDetalles()) {
            inventarioService.aumentarStock(detalle.getProducto().getIdProducto(), 
                detalle.getCantidad().intValue());
            detalle.setCantidadRecibida(detalle.getCantidad());
        }
        
        compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        compra.setFechaEntregaReal(LocalDate.now());
        compraRepository.save(compra);
        
        if (compra.getTipoPago() == Compra.TipoPago.CREDITO) {
            proveedorService.actualizarSaldo(compra.getProveedor().getIdProveedor(), compra.getTotalCompra(), true);
        }
    }
    
    public void recibirCompraParcial(Integer id, Integer detalleId, BigDecimal cantidadRecibida) {
        Compra compra = obtenerCompraPorId(id);
        DetalleCompra detalle = detalleCompraRepository.findById(detalleId)
            .orElseThrow(() -> new RuntimeException("Detalle de compra no encontrado"));
        
        if (cantidadRecibida.compareTo(detalle.getCantidad()) > 0) {
            throw new RuntimeException("La cantidad recibida excede la cantidad pedida");
        }
        
        detalle.setCantidadRecibida(detalle.getCantidadRecibida().add(cantidadRecibida));
        detalleCompraRepository.save(detalle);
        
        inventarioService.aumentarStock(detalle.getProducto().getIdProducto(), cantidadRecibida.intValue());
        
        boolean todasRecibidas = compra.getDetalles().stream()
            .allMatch(d -> d.getCantidadRecibida().compareTo(d.getCantidad()) >= 0);
        
        if (todasRecibidas) {
            compra.setEstado(Compra.EstadoCompra.RECIBIDA);
        } else {
            compra.setEstado(Compra.EstadoCompra.PARCIAL);
        }
        
        compraRepository.save(compra);
    }
    
    public void anularCompra(Integer id) {
        Compra compra = obtenerCompraPorId(id);
        if (compra.getEstado() == Compra.EstadoCompra.RECIBIDA) {
            throw new RuntimeException("No se puede anular una compra ya recibida");
        }
        compra.setEstado(Compra.EstadoCompra.ANULADA);
        compraRepository.save(compra);
    }
    
    private void calcularTotalesDetalle(DetalleCompra detalle) {
        BigDecimal subtotal = detalle.getPrecioUnitario()
            .multiply(detalle.getCantidad());
        detalle.setSubtotal(subtotal.subtract(detalle.getDescuento()));
        
        BigDecimal tasaIva = new BigDecimal("0.16");
        detalle.setImpuesto(detalle.getSubtotal().multiply(tasaIva));
        detalle.setTotal(detalle.getSubtotal().add(detalle.getImpuesto()));
    }
    
    private void calcularTotalesCompra(Compra compra) {
        BigDecimal subtotal = compra.getDetalles().stream()
            .map(DetalleCompra::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal impuestos = compra.getDetalles().stream()
            .map(DetalleCompra::getImpuesto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        compra.setSubtotal(subtotal);
        compra.setImpuestos(impuestos);
        compra.setTotalCompra(subtotal.add(impuestos).subtract(compra.getDescuento()));
    }
    
    private String generarNumeroCompra() {
        return "COM-" + System.currentTimeMillis();
    }
    
    @Transactional(readOnly = true)
    public Compra obtenerCompraPorId(Integer id) {
        return compraRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }
    
    @Transactional(readOnly = true)
    public List<Compra> obtenerTodasLasCompras() {
        return compraRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Compra> obtenerComprasPorPeriodo(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio, fin);
    }
}
