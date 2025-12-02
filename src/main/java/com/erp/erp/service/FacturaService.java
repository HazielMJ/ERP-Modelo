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
    
    /**
     * Crear una nueva factura
     * Si está asociada a una venta, copia automáticamente los datos
     */
    public Factura crearFactura(Factura factura) {
        // Generar número único de factura
        factura.setNumeroFactura(generarNumeroFactura());
        
        // Si está asociada a una venta, copiar datos
        if (factura.getVenta() != null && factura.getVenta().getIdVenta() != null) {
            Venta venta = ventasRepository.findById(factura.getVenta().getIdVenta())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + factura.getVenta().getIdVenta()));
            
            // Copiar datos de la venta a la factura
            factura.setSubtotal(venta.getSubtotal());
            factura.setImpuestos(venta.getImpuestos());
            factura.setDescuentos(venta.getDescuento());
            factura.setTotal(venta.getTotalVenta());
            
            // Si no se especificó cliente, tomar el de la venta
            if (factura.getCliente() == null) {
                factura.setCliente(venta.getCliente());
            }
        }
        
        return facturaRepository.save(factura);
    }
    
    /**
     * Actualizar una factura existente
     * Solo permite actualizar facturas que NO estén pagadas
     */
    public Factura actualizarFactura(Integer id, Factura factura) {
        Factura existente = obtenerFacturaPorId(id);
        
        // Validar que no esté pagada
        if (existente.getEstado() == Factura.EstadoFactura.PAGADA) {
            throw new RuntimeException("No se puede modificar una factura pagada");
        }
        
        // Actualizar solo campos permitidos
        existente.setFechaVencimiento(factura.getFechaVencimiento());
        existente.setUsoCfdi(factura.getUsoCfdi());
        existente.setMetodoPago(factura.getMetodoPago());
        
        return facturaRepository.save(existente);
    }
    
    /**
     * Marcar factura como pagada
     * Reduce el saldo del cliente automáticamente
     */
    public void marcarComoPagada(Integer id) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (factura.getEstado() == Factura.EstadoFactura.PAGADA) {
            throw new RuntimeException("La factura ya está marcada como pagada");
        }
        
        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("No se puede marcar como pagada una factura anulada");
        }
        
        // Cambiar estado a PAGADA
        factura.setEstado(Factura.EstadoFactura.PAGADA);
        facturaRepository.save(factura);
        
        // Reducir saldo del cliente
        if (factura.getCliente() != null) {
            clienteService.actualizarSaldo(
                factura.getCliente().getIdCliente(), 
                factura.getTotal(), 
                false // false = restar del saldo
            );
        }
    }
    
    /**
     * Timbrar factura con UUID del SAT
     */
    public void timbrarFactura(Integer id, String uuid) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new RuntimeException("El UUID fiscal no puede estar vacío");
        }
        
        factura.setUuidFiscal(uuid);
        facturaRepository.save(factura);
    }
    
    /**
     * Anular una factura
     * Una vez anulada, no se puede revertir
     */
    public void anularFactura(Integer id) {
        Factura factura = obtenerFacturaPorId(id);
        
        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("La factura ya está anulada");
        }
        
        // Si estaba pagada, devolver el saldo al cliente
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
    
    /**
     * Verificar y marcar facturas vencidas
     * Este método debería ejecutarse diariamente (con @Scheduled)
     */
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
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtener una factura por ID
     */
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorId(Integer id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
    }
    
    /**
     * Obtener todas las facturas
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerTodasLasFacturas() {
        return facturaRepository.findAll();
    }
    
    /**
     * Obtener facturas de un cliente específico
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorCliente(Integer clienteId) {
        Cliente cliente = clienteService.obtenerClientePorId(clienteId);
        return facturaRepository.findByCliente(cliente);
    }
    
    /**
     * Obtener facturas por rango de fechas de emisión
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorPeriodo(LocalDate inicio, LocalDate fin) {
        return facturaRepository.findByFechaEmisionBetween(inicio, fin);
    }
    
    /**
     * Obtener facturas por estado
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPorEstado(Factura.EstadoFactura estado) {
        return facturaRepository.findByEstado(estado);
    }
    
    /**
     * Obtener facturas pendientes de pago
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasPendientes() {
        return facturaRepository.findByEstado(Factura.EstadoFactura.PENDIENTE);
    }
    
    /**
     * Obtener facturas vencidas
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerFacturasVencidas() {
        return facturaRepository.findFacturasVencidas(LocalDate.now());
    }
}
