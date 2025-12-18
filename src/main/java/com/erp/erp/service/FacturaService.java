package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final VentasRepository ventasRepository; // ⬅️ AGREGADO PARA ACCESO DIRECTO
    private final ClienteService clienteService;
    
    public Factura crearFactura(Factura factura) {

        factura.setNumeroFactura(generarNumeroFactura());
        

        if (factura.getVenta() != null && factura.getVenta().getIdVenta() != null) {
            Venta venta = ventasRepository.findById(factura.getVenta().getIdVenta())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + factura.getVenta().getIdVenta()));
            
            factura.setSubtotal(venta.getSubtotal());
            factura.setImpuestos(venta.getImpuestos());
            factura.setDescuentos(venta.getDescuento());
            factura.setTotal(venta.getTotalVenta());
            
            if (factura.getCliente() == null) {
                factura.setCliente(venta.getCliente());
            }
        }
        
        return facturaRepository.save(factura);
    }

    public Factura actualizarFactura(Integer id, Factura factura) {
        Factura existente = obtenerFacturaPorId(id);
        
        if (existente.getEstado() == Factura.EstadoFactura.PAGADA) {
            throw new RuntimeException("No se puede modificar una factura pagada");
        }
        existente.setFechaVencimiento(factura.getFechaVencimiento());
        existente.setUsoCfdi(factura.getUsoCfdi());
        existente.setMetodoPago(factura.getMetodoPago());
        
        return facturaRepository.save(existente);
    }
    

    public void marcarComoPagada(Integer id) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (factura.getEstado() == Factura.EstadoFactura.PAGADA) {
            throw new RuntimeException("La factura ya está marcada como pagada");
        }
        
        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("No se puede marcar como pagada una factura anulada");
        }
        
        factura.setEstado(Factura.EstadoFactura.PAGADA);
        facturaRepository.save(factura);
        
        if (factura.getCliente() != null) {
            clienteService.actualizarSaldo(
                factura.getCliente().getIdCliente(), 
                factura.getTotal(), 
                false // false = restar del saldo
            );
        }
    }
    
    public void timbrarFactura(Integer id, String uuid) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new RuntimeException("El UUID fiscal no puede estar vacío");
        }
        
        factura.setUuidFiscal(uuid);
        facturaRepository.save(factura);
    }
    
    public void anularFactura(Integer id) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("La factura ya está anulada");
        }
        

        if (factura.getEstado() == Factura.EstadoFactura.PAGADA && factura.getCliente() != null) {
            clienteService.actualizarSaldo(
                factura.getCliente().getIdCliente(), 
                factura.getTotal(), 
                true // true = sumar al saldo (devolver)
            );
        }
        
        factura.setEstado(Factura.EstadoFactura.ANULADA);
        facturaRepository.save(factura);
    }
    
    
    public void verificarFacturasVencidas() {
        LocalDate hoy = LocalDate.now();
        List<Factura> vencidas = facturaRepository.findFacturasVencidas(hoy);
        
        for (Factura factura : vencidas) {
            if (factura.getEstado() == Factura.EstadoFactura.PENDIENTE) {
                factura.setEstado(Factura.EstadoFactura.VENCIDA);
                facturaRepository.save(factura);
            }
        }
    }
    
    /**
     * Generar número único de factura
     * Formato: FAC-{timestamp}
     */
    private String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis();
    }
    
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorId(Integer id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
    }
    

    @Transactional(readOnly = true)
    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorCliente(Integer clienteId) {
        Cliente cliente = clienteService.obtenerClientePorId(clienteId);
        return facturaRepository.findByCliente(cliente);
    }
    
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorPeriodo(LocalDate inicio, LocalDate fin) {
        return facturaRepository.findByFechaEmisionBetween(inicio, fin);
    }
    
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorEstado(Factura.EstadoFactura estado) {
        return facturaRepository.findByEstado(estado);
    }
    
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPendientes() {
        return facturaRepository.findByEstado(Factura.EstadoFactura.PENDIENTE);
    }
    
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasVencidas() {
        return facturaRepository.findFacturasVencidas(LocalDate.now());
    }
}
