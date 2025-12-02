package com.erp.erp.service;

import com.erp.erp.dto.VentaDTO;
import com.erp.erp.entity.*;
import com.erp.erp.exception.ResourceNotFoundException;
import com.erp.erp.exception.BusinessException;
import com.erp.erp.mapper.VentaMapper;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VentaService {
    
    private final VentasRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final VentaMapper ventaMapper;
    
    /**
     * Crear venta completa (usado en Punto de Venta)
     * Procesa la venta, descuenta stock y registra todo en una transacción
     */
    public VentaDTO crearVenta(VentaDTO ventaDTO) {
        log.info("═══════════════════════════════════════");
        log.info("Iniciando creación de venta");
        log.info("═══════════════════════════════════════");
        
        // 1. VALIDAR CLIENTE
        Cliente cliente = clienteRepository.findById(ventaDTO.getCliente().getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente no encontrado con ID: " + ventaDTO.getCliente().getIdCliente()));
        log.info("✓ Cliente validado: {}", cliente.getNombreRazonSocial());
        
        // 2. VALIDAR USUARIO
        Usuario usuario = usuarioRepository.findById(ventaDTO.getUsuario().getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado con ID: " + ventaDTO.getUsuario().getId()));
        log.info("✓ Usuario validado: {}", usuario.getNombre());
        
        // 3. VALIDAR QUE HAYA PRODUCTOS
        if (ventaDTO.getDetalles() == null || ventaDTO.getDetalles().isEmpty()) {
            throw new BusinessException("La venta debe tener al menos un producto");
        }
        log.info("✓ Venta contiene {} productos", ventaDTO.getDetalles().size());
        
        // 4. VALIDAR STOCK DE CADA PRODUCTO ANTES DE GUARDAR
        log.info("Validando disponibilidad de stock...");
        for (VentaDTO.DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProducto().getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Producto no encontrado con ID: " + detalleDTO.getProducto().getIdProducto()));
            
            int stockDisponible = inventarioService.obtenerStockDisponible(producto.getIdProducto());
            int cantidadSolicitada = detalleDTO.getCantidad().intValue();
            
            if (stockDisponible < cantidadSolicitada) {
                throw new BusinessException(
                    String.format("❌ Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                        producto.getNombreProducto(), stockDisponible, cantidadSolicitada)
                );
            }
            
            log.info("  ✓ {} - Stock OK (Disponible: {}, Solicitado: {})", 
                producto.getNombreProducto(), stockDisponible, cantidadSolicitada);
        }
        
        // 5. CONVERTIR DTO A ENTITY
        Venta venta = ventaMapper.toEntity(ventaDTO);
        
        // 6. ESTABLECER RELACIONES
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        
        // 7. GENERAR NÚMERO DE VENTA
        venta.setNumeroVenta(generarNumeroVenta());
        log.info("✓ Número de venta generado: {}", venta.getNumeroVenta());
        
        // 8. PROCESAR DETALLES Y CALCULAR TOTALES
        log.info("Procesando detalles de venta...");
        for (DetalleVenta detalle : venta.getDetalles()) {
            // Cargar producto completo
            Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            detalle.setProducto(producto);
            
            // Calcular totales del detalle
            calcularTotalesDetalle(detalle);
            
            log.info("  - {} x {} = ${}", 
                detalle.getProducto().getNombreProducto(),
                detalle.getCantidad(),
                detalle.getTotal());
        }
        
        // 9. CALCULAR TOTALES DE LA VENTA
        venta.calcularTotales();
        log.info("✓ Totales calculados - Subtotal: ${}, IVA: ${}, Total: ${}", 
            venta.getSubtotal(), venta.getImpuestos(), venta.getTotalVenta());
        
        // 10. VALIDAR MONTO RECIBIDO Y CALCULAR CAMBIO
        if (venta.getMontoRecibido() != null) {
            if (venta.getMontoRecibido().compareTo(venta.getTotalVenta()) >= 0) {
                venta.setEstado(Venta.EstadoVenta.PAGADA);
                BigDecimal cambio = venta.getMontoRecibido().subtract(venta.getTotalVenta());
                venta.setCambio(cambio);
                log.info("✓ Venta marcada como PAGADA - Cambio: ${}", cambio);
            } else {
                throw new BusinessException(
                    String.format("Monto recibido ($%.2f) es menor al total ($%.2f)",
                        venta.getMontoRecibido(), venta.getTotalVenta())
                );
            }
        } else {
            venta.setEstado(Venta.EstadoVenta.PENDIENTE);
            log.info("✓ Venta marcada como PENDIENTE");
        }
        
        // 11. GUARDAR VENTA EN BASE DE DATOS
        Venta ventaGuardada = ventaRepository.save(venta);
        log.info("✓ Venta guardada en base de datos con ID: {}", ventaGuardada.getIdVenta());
        
        // 12. DESCONTAR STOCK DEL INVENTARIO
        log.info("Descontando stock del inventario...");
        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
            try {
                inventarioService.descontarStock(
                    detalle.getProducto().getIdProducto(), 
                    1, // Almacén principal (ID=1)
                    detalle.getCantidad().intValue()
                );
                log.info("  ✓ Stock descontado: {} unidades de '{}'", 
                    detalle.getCantidad(), detalle.getProducto().getNombreProducto());
            } catch (Exception e) {
                log.error("  ❌ Error al descontar stock: {}", e.getMessage());
                throw new BusinessException("Error al descontar stock: " + e.getMessage());
            }
        }
        
        // 13. ACTUALIZAR SALDO DEL CLIENTE (si es a crédito)
        if (venta.getTipoPago() == Venta.TipoPago.CREDITO && venta.isPagada()) {
            actualizarSaldoCliente(cliente, venta.getTotalVenta(), true);
            log.info("✓ Saldo del cliente actualizado");
        }
        
        log.info("═══════════════════════════════════════");
        log.info("✅ VENTA CREADA EXITOSAMENTE");
        log.info("Número: {}", ventaGuardada.getNumeroVenta());
        log.info("Total: ${}", ventaGuardada.getTotalVenta());
        log.info("Estado: {}", ventaGuardada.getEstado());
        log.info("═══════════════════════════════════════");
        
        return ventaMapper.toDTO(ventaGuardada);
    }
    
    /**
     * Agregar detalle a una venta existente
     */
    public VentaDTO agregarDetalleVenta(Integer ventaId, VentaDTO.DetalleVentaDTO detalleDTO) {
        log.info("Agregando detalle a venta ID: {}", ventaId);
        
        Venta venta = obtenerVentaEntity(ventaId);
        
        if (!venta.isPendiente()) {
            throw new BusinessException(
                "No se pueden agregar productos a una venta " + venta.getEstado());
        }
        
        // Validar producto
        Producto producto = productoRepository.findById(detalleDTO.getProducto().getIdProducto())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        // Validar stock
        int stockDisponible = inventarioService.obtenerStockDisponible(producto.getIdProducto());
        if (stockDisponible < detalleDTO.getCantidad().intValue()) {
            throw new BusinessException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    stockDisponible, detalleDTO.getCantidad().intValue())
            );
        }
        
        // Crear detalle
        DetalleVenta detalle = DetalleVenta.builder()
            .venta(venta)
            .producto(producto)
            .cantidad(detalleDTO.getCantidad())
            .precioUnitario(detalleDTO.getPrecioUnitario())
            .descuento(detalleDTO.getDescuento())
            .build();
        
        calcularTotalesDetalle(detalle);
        venta.addDetalle(detalle);
        venta.calcularTotales();
        
        Venta ventaActualizada = ventaRepository.save(venta);
        log.info("✓ Detalle agregado exitosamente");
        
        return ventaMapper.toDTO(ventaActualizada);
    }
    
    /**
     * Procesar pago de una venta pendiente
     */
    public VentaDTO procesarPagoVenta(Integer id, BigDecimal montoRecibido) {
        log.info("Procesando pago de venta ID: {}", id);
        
        Venta venta = obtenerVentaEntity(id);
        
        if (!venta.isPendiente()) {
            throw new BusinessException("La venta ya fue procesada");
        }
        
        if (montoRecibido.compareTo(venta.getTotalVenta()) < 0) {
            throw new BusinessException("El monto recibido es menor al total de la venta");
        }
        
        venta.setMontoRecibido(montoRecibido);
        venta.setCambio(montoRecibido.subtract(venta.getTotalVenta()));
        venta.setEstado(Venta.EstadoVenta.PAGADA);
        
        Venta ventaActualizada = ventaRepository.save(venta);
        
        // Descontar stock
        for (DetalleVenta detalle : venta.getDetalles()) {
            inventarioService.descontarStock(
                detalle.getProducto().getIdProducto(), 
                1,
                detalle.getCantidad().intValue()
            );
        }
        
        // Actualizar saldo del cliente si es a crédito
        if (venta.getTipoPago() == Venta.TipoPago.CREDITO) {
            actualizarSaldoCliente(venta.getCliente(), venta.getTotalVenta(), true);
        }
        
        log.info("✓ Venta pagada exitosamente");
        return ventaMapper.toDTO(ventaActualizada);
    }
    
    /**
     * Anular venta (devuelve stock al inventario)
     */
    public void anularVenta(Integer id) {
        log.info("Anulando venta ID: {}", id);
        
        Venta venta = obtenerVentaEntity(id);
        
        if (venta.isAnulada()) {
            throw new BusinessException("La venta ya está anulada");
        }
        
        // Devolver stock al inventario solo si ya estaba pagada
        if (venta.isPagada()) {
            log.info("Devolviendo stock al inventario...");
            for (DetalleVenta detalle : venta.getDetalles()) {
                inventarioService.aumentarStock(
                    detalle.getProducto().getIdProducto(),
                    1,
                    detalle.getCantidad().intValue()
                );
                log.info("  ✓ Stock devuelto: {} unidades de '{}'", 
                    detalle.getCantidad(), detalle.getProducto().getNombreProducto());
            }
            
            // Actualizar saldo del cliente si fue a crédito
            if (venta.getTipoPago() == Venta.TipoPago.CREDITO) {
                actualizarSaldoCliente(venta.getCliente(), venta.getTotalVenta(), false);
            }
        }
        
        venta.setEstado(Venta.EstadoVenta.ANULADA);
        ventaRepository.save(venta);
        
        log.info("✅ Venta anulada exitosamente");
    }
    
    /**
     * Calcular totales de un detalle de venta
     */
    private void calcularTotalesDetalle(DetalleVenta detalle) {
        // Subtotal = (cantidad * precio) - descuento
        BigDecimal subtotal = detalle.getPrecioUnitario()
            .multiply(detalle.getCantidad())
            .subtract(detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO);
        detalle.setSubtotal(subtotal);
        
        // IVA 16% (verificar si el producto aplica IVA)
        BigDecimal tasaIva = detalle.getProducto().getAplicaIva() ? 
            new BigDecimal("0.16") : BigDecimal.ZERO;
        BigDecimal impuesto = subtotal.multiply(tasaIva);
        detalle.setImpuesto(impuesto);
        
        // Total = subtotal + impuesto
        detalle.setTotal(subtotal.add(impuesto));
    }
    
    /**
     * Generar número único de venta
     */
    private String generarNumeroVenta() {
        return "VTA-" + System.currentTimeMillis();
    }
    
    /**
     * Actualizar saldo de cliente
     */
    private void actualizarSaldoCliente(Cliente cliente, BigDecimal monto, boolean aumentar) {
        BigDecimal saldoActual = cliente.getSaldoActual() != null ? 
            cliente.getSaldoActual() : BigDecimal.ZERO;
        
        if (aumentar) {
            cliente.setSaldoActual(saldoActual.add(monto));
            log.info("Saldo cliente aumentado: ${} -> ${}", saldoActual, cliente.getSaldoActual());
        } else {
            cliente.setSaldoActual(saldoActual.subtract(monto));
            log.info("Saldo cliente disminuido: ${} -> ${}", saldoActual, cliente.getSaldoActual());
        }
        
        clienteRepository.save(cliente);
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    @Transactional(readOnly = true)
    public VentaDTO obtenerVentaPorId(Integer id) {
        Venta venta = obtenerVentaEntity(id);
        return ventaMapper.toDTO(venta);
    }
    
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerTodasLasVentas() {
        return ventaRepository.findAll().stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasPorEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado).stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasPorCliente(Integer clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        
        return ventaRepository.findByCliente(cliente).stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin).stream()
            .map(ventaMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        BigDecimal total = ventaRepository.getTotalVentasByPeriodo(inicio, fin);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerVentasDelDia() {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finDia = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return obtenerVentasPorPeriodo(inicioDia, finDia);
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private Venta obtenerVentaEntity(Integer id) {
        return ventaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Venta no encontrada con ID: " + id));
    }
}