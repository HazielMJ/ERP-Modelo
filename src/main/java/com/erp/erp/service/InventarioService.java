package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final UsuarioRepository usuarioRepository;
    
}
    public Inventario obtenerOCrearInventario(Integer productoId, Integer almacenId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        Almacen almacen = almacenRepository.findById(almacenId)
            .orElseThrow(() -> new RuntimeException("Almacén no encontrado con ID: " + almacenId));
        
        return inventarioRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseGet(() -> {
                log.info("Creando nuevo registro de inventario para producto {} en almacén {}", 
                    productoId, almacenId);
                Inventario nuevo = Inventario.builder()
                    .producto(producto)
                    .almacen(almacen)
                    .stockActual(0)
                    .stockReservado(0)
                    .fechaUltimoMovimiento(LocalDateTime.now())
                    .build();
                return inventarioRepository.save(nuevo);
            });
    }
    
    @Transactional(readOnly = true)
    public int obtenerStockDisponible(Integer productoId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        List<Inventario> inventarios = inventarioRepository.findByProducto(producto);
        
        int stockTotal = inventarios.stream()
            .mapToInt(inv -> inv.getStockActual() - inv.getStockReservado())
            .sum();
        
        log.debug("Stock disponible para producto {}: {}", productoId, stockTotal);
        return stockTotal;
    }
    
    @Transactional(readOnly = true)
    public boolean verificarStockDisponible(Integer productoId, Integer cantidad) {
        int disponible = obtenerStockDisponible(productoId);
        return disponible >= cantidad;
    }
    
    public void aumentarStock(Integer productoId, Integer cantidad) {
        aumentarStock(productoId, 1, cantidad); // Almacén por defecto ID=1
    }
    
    public void aumentarStock(Integer productoId, Integer almacenId, Integer cantidad) {
        log.info("Aumentando stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        Inventario inventario = obtenerOCrearInventario(productoId, almacenId);
        

        inventario.setStockActual(inventario.getStockActual() + cantidad);
        inventario.setFechaUltimoMovimiento(LocalDateTime.now());
        inventarioRepository.save(inventario);
        

        registrarMovimiento(
            productoId, 
            null, 
            almacenId, 
            new BigDecimal(cantidad), 
            MovimientoInventario.TipoMovimiento.ENTRADA,
            MovimientoInventario.TipoReferencia.AJUSTE,
            null,
            "Entrada de inventario"
        );
        
        log.info("Stock aumentado exitosamente. Nuevo stock: {}", inventario.getStockActual());
    }
    

    public void descontarStock(Integer productoId, Integer cantidad) {
        descontarStock(productoId, 1, cantidad); // Almacén por defecto ID=1
    }
    

    public void descontarStock(Integer productoId, Integer almacenId, Integer cantidad) {
        log.info("Descontando stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        Inventario inventario = obtenerOCrearInventario(productoId, almacenId);
        

        int disponible = inventario.getStockActual() - inventario.getStockReservado();
        if (disponible < cantidad) {
            throw new RuntimeException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                    disponible, cantidad)
            );
        }
        

        inventario.setStockActual(inventario.getStockActual() - cantidad);
        inventario.setFechaUltimoMovimiento(LocalDateTime.now());
        inventarioRepository.save(inventario);
        

        registrarMovimiento(
            productoId, 
            almacenId, 
            null, 
            new BigDecimal(cantidad), 
            MovimientoInventario.TipoMovimiento.SALIDA,
            MovimientoInventario.TipoReferencia.VENTA,
            null,
            "Salida por venta"
        );
        
        log.info("Stock descontado exitosamente. Nuevo stock: {}", inventario.getStockActual());
        
        Producto producto = inventario.getProducto();
        if (inventario.getStockActual() <= producto.getStockMinimo()) {
            log.warn("¡ALERTA! Stock crítico para producto {}: {} unidades", 
                producto.getNombreProducto(), inventario.getStockActual());
        }
    }
    

    public void reservarStock(Integer productoId, Integer almacenId, Integer cantidad) {
        log.info("Reservando stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        Inventario inventario = obtenerOCrearInventario(productoId, almacenId);
        
        int disponible = inventario.getStockActual() - inventario.getStockReservado();
        if (disponible < cantidad) {
            throw new RuntimeException("Stock insuficiente para reservar");
        }
        
        inventario.setStockReservado(inventario.getStockReservado() + cantidad);
        inventarioRepository.save(inventario);
        
        log.info("Stock reservado exitosamente");
    }
    

    public void liberarStockReservado(Integer productoId, Integer almacenId, Integer cantidad) {
        log.info("Liberando stock reservado: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        Inventario inventario = obtenerOCrearInventario(productoId, almacenId);
        
        int nuevoReservado = Math.max(0, inventario.getStockReservado() - cantidad);
        inventario.setStockReservado(nuevoReservado);
        inventarioRepository.save(inventario);
        
        log.info("Stock liberado exitosamente");
    }
    

    public void transferirStock(Integer productoId, Integer almacenOrigenId, 
                                Integer almacenDestinoId, Integer cantidad) {
        log.info("Transfiriendo stock: producto={}, origen={}, destino={}, cantidad={}", 
            productoId, almacenOrigenId, almacenDestinoId, cantidad);
        
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        if (almacenOrigenId.equals(almacenDestinoId)) {
            throw new RuntimeException("Los almacenes origen y destino deben ser diferentes");
        }
        
        Inventario origen = obtenerOCrearInventario(productoId, almacenOrigenId);
        Inventario destino = obtenerOCrearInventario(productoId, almacenDestinoId);
        
        int disponible = origen.getStockActual() - origen.getStockReservado();
        if (disponible < cantidad) {
            throw new RuntimeException("Stock insuficiente en almacén origen");
        }
        
        origen.setStockActual(origen.getStockActual() - cantidad);
        destino.setStockActual(destino.getStockActual() + cantidad);
        
        LocalDateTime ahora = LocalDateTime.now();
        origen.setFechaUltimoMovimiento(ahora);
        destino.setFechaUltimoMovimiento(ahora);
        
        inventarioRepository.save(origen);
        inventarioRepository.save(destino);
        

        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Almacen almacenOrigen = almacenRepository.findById(almacenOrigenId).orElseThrow();
        Almacen almacenDestino = almacenRepository.findById(almacenDestinoId).orElseThrow();
        
        MovimientoInventario movimiento = MovimientoInventario.builder()
            .tipoMovimiento(MovimientoInventario.TipoMovimiento.TRANSFERENCIA)
            .producto(producto)
            .almacenOrigen(almacenOrigen)
            .almacenDestino(almacenDestino)
            .cantidad(new BigDecimal(cantidad))
            .referenciaTipo(MovimientoInventario.TipoReferencia.TRANSFERENCIA)
            .usuario(usuarioRepository.findById(1).orElse(null))
            .observaciones("Transferencia entre almacenes")
            .build();
        
        movimientoRepository.save(movimiento);
        
        log.info("Transferencia completada exitosamente");
    }
    
    public void ajustarStock(Integer productoId, Integer almacenId, 
                            Integer nuevoStock, String observacion) {
        log.info("Ajustando stock: producto={}, almacén={}, nuevoStock={}", 
            productoId, almacenId, nuevoStock);
        
        if (nuevoStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        
        Inventario inventario = obtenerOCrearInventario(productoId, almacenId);
        Integer stockAnterior = inventario.getStockActual();
        Integer diferencia = nuevoStock - stockAnterior;
        
        inventario.setStockActual(nuevoStock);
        inventario.setFechaUltimoMovimiento(LocalDateTime.now());
        inventarioRepository.save(inventario);
        
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        Almacen almacen = almacenRepository.findById(almacenId).orElseThrow();
        
        MovimientoInventario movimiento = MovimientoInventario.builder()
            .tipoMovimiento(MovimientoInventario.TipoMovimiento.AJUSTE)
            .producto(producto)
            .almacenDestino(almacen)
            .cantidad(new BigDecimal(Math.abs(diferencia)))
            .referenciaTipo(MovimientoInventario.TipoReferencia.AJUSTE)
            .observaciones(String.format("Ajuste de inventario. Stock anterior: %d, Nuevo: %d. %s", 
                stockAnterior, nuevoStock, observacion != null ? observacion : ""))
            .usuario(usuarioRepository.findById(1).orElse(null))
            .build();
        
        movimientoRepository.save(movimiento);
        
        log.info("Stock ajustado exitosamente. Diferencia: {}", diferencia);
    }
    

    private void registrarMovimiento(Integer productoId, Integer almacenOrigenId, 
                                     Integer almacenDestinoId, BigDecimal cantidad,
                                     MovimientoInventario.TipoMovimiento tipo,
                                     MovimientoInventario.TipoReferencia tipoReferencia,
                                     Integer referenciaId, String observaciones) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Almacen almacenOrigen = almacenOrigenId != null ? 
            almacenRepository.findById(almacenOrigenId).orElse(null) : null;
        Almacen almacenDestino = almacenDestinoId != null ? 
            almacenRepository.findById(almacenDestinoId).orElse(null) : null;
        
        MovimientoInventario movimiento = MovimientoInventario.builder()
            .tipoMovimiento(tipo)
            .producto(producto)
            .almacenOrigen(almacenOrigen)
            .almacenDestino(almacenDestino)
            .cantidad(cantidad)
            .referenciaTipo(tipoReferencia)
            .referenciaId(referenciaId)
            .observaciones(observaciones)
            .usuario(usuarioRepository.findById(1).orElse(null))
            .build();
        
        movimientoRepository.save(movimiento);
        log.debug("Movimiento de inventario registrado: tipo={}, cantidad={}", tipo, cantidad);
    }
    

    @Transactional(readOnly = true)
    public List<Inventario> obtenerInventarioPorProducto(Integer productoId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return inventarioRepository.findByProducto(producto);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerHistorialMovimientos(Integer productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId);
    }
    

    @Transactional(readOnly = true)
    public List<Inventario> obtenerInventarioBajoStock() {
        return inventarioRepository.findInventarioBajoStock();
    }
    
    @Transactional(readOnly = true)
    public List<Inventario> obtenerInventarioPorAlmacen(Integer almacenId) {
        Almacen almacen = almacenRepository.findById(almacenId)
            .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
        return inventarioRepository.findByAlmacen(almacen);
    }
    

    @Transactional(readOnly = true)
    public Inventario obtenerStockEnAlmacen(Integer productoId, Integer almacenId) {
        return inventarioRepository.findByProductoIdAndAlmacenId(productoId, almacenId)
            .orElse(null);
    }

@Transactional(readOnly = true)
public Integer getStockDisponibleByProducto(Integer productoId) {
    return inventarioRepository.getStockDisponibleByProducto(productoId);
}

}
