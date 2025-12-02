package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class InventarioController {
    private final InventarioService inventarioService;
    
    /**
     * Aumentar stock de un producto en un almacén
     * POST /api/inventario/aumentar-stock?productoId=1&almacenId=1&cantidad=10
     */
    @PostMapping("/aumentar-stock")
    public ResponseEntity<Map<String, Object>> aumentarStock(
            @RequestParam Integer productoId,
            @RequestParam(required = false, defaultValue = "1") Integer almacenId,
            @RequestParam Integer cantidad) {
        
        log.info("Solicitud para aumentar stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        try {
            inventarioService.aumentarStock(productoId, almacenId, cantidad);
            
            int stockActual = inventarioService.obtenerStockDisponible(productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock aumentado exitosamente");
            response.put("productoId", productoId);
            response.put("cantidadAgregada", cantidad);
            response.put("stockActual", stockActual);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al aumentar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Descontar stock de un producto
     * POST /api/inventario/descontar-stock?productoId=1&almacenId=1&cantidad=5
     */
    @PostMapping("/descontar-stock")
    public ResponseEntity<Map<String, Object>> descontarStock(
            @RequestParam Integer productoId,
            @RequestParam(required = false, defaultValue = "1") Integer almacenId,
            @RequestParam Integer cantidad) {
        
        log.info("Solicitud para descontar stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        try {
            inventarioService.descontarStock(productoId, almacenId, cantidad);
            
            int stockActual = inventarioService.obtenerStockDisponible(productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock descontado exitosamente");
            response.put("productoId", productoId);
            response.put("cantidadDescontada", cantidad);
            response.put("stockActual", stockActual);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al descontar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Transferir stock entre almacenes
     * POST /api/inventario/transferir?productoId=1&almacenOrigenId=1&almacenDestinoId=2&cantidad=10
     */
    @PostMapping("/transferir")
    public ResponseEntity<Map<String, Object>> transferirStock(
            @RequestParam Integer productoId,
            @RequestParam Integer almacenOrigenId,
            @RequestParam Integer almacenDestinoId,
            @RequestParam Integer cantidad) {
        
        log.info("Solicitud de transferencia: producto={}, origen={}, destino={}, cantidad={}", 
            productoId, almacenOrigenId, almacenDestinoId, cantidad);
        
        try {
            inventarioService.transferirStock(productoId, almacenOrigenId, almacenDestinoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transferencia exitosa");
            response.put("productoId", productoId);
            response.put("cantidad", cantidad);
            response.put("almacenOrigen", almacenOrigenId);
            response.put("almacenDestino", almacenDestinoId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en transferencia", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Ajustar stock manualmente (inventario físico)
     * POST /api/inventario/ajustar?productoId=1&almacenId=1&nuevoStock=100&observacion=Inventario físico
     */
    @PostMapping("/ajustar")
    public ResponseEntity<Map<String, Object>> ajustarStock(
            @RequestParam Integer productoId,
            @RequestParam Integer almacenId,
            @RequestParam Integer nuevoStock,
            @RequestParam(required = false) String observacion) {
        
        log.info("Solicitud de ajuste: producto={}, almacén={}, nuevoStock={}", 
            productoId, almacenId, nuevoStock);
        
        try {
            inventarioService.ajustarStock(productoId, almacenId, nuevoStock, observacion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock ajustado exitosamente");
            response.put("productoId", productoId);
            response.put("nuevoStock", nuevoStock);
            response.put("observacion", observacion);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al ajustar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Obtener inventario de un producto en todos los almacenes
     * GET /api/inventario/producto/1
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Inventario>> obtenerInventarioPorProducto(
            @PathVariable Integer productoId) {
        
        log.info("Consultando inventario del producto ID: {}", productoId);
        List<Inventario> inventarios = inventarioService.obtenerInventarioPorProducto(productoId);
        return ResponseEntity.ok(inventarios);
    }
    
    /**
     * Obtener stock disponible de un producto (suma de todos los almacenes)
     * GET /api/inventario/disponible/1
     */
    @GetMapping("/disponible/{productoId}")
    public ResponseEntity<Map<String, Object>> obtenerStockDisponible(
            @PathVariable Integer productoId) {
        
        log.info("Consultando stock disponible del producto ID: {}", productoId);
        
        try {
            int stockDisponible = inventarioService.obtenerStockDisponible(productoId);
            boolean hayStock = inventarioService.verificarStockDisponible(productoId, 1);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productoId", productoId);
            response.put("stockDisponible", stockDisponible);
            response.put("hayStock", hayStock);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al consultar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Verificar si hay stock suficiente
     * GET /api/inventario/verificar/1?cantidad=10
     */
    @GetMapping("/verificar/{productoId}")
    public ResponseEntity<Map<String, Object>> verificarStock(
            @PathVariable Integer productoId,
            @RequestParam Integer cantidad) {
        
        log.info("Verificando stock: producto={}, cantidad={}", productoId, cantidad);
        
        try {
            boolean disponible = inventarioService.verificarStockDisponible(productoId, cantidad);
            int stockActual = inventarioService.obtenerStockDisponible(productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productoId", productoId);
            response.put("cantidadSolicitada", cantidad);
            response.put("stockDisponible", stockActual);
            response.put("disponible", disponible);
            response.put("faltante", disponible ? 0 : (cantidad - stockActual));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al verificar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Obtener historial de movimientos de un producto
     * GET /api/inventario/movimientos/1
     */
    @GetMapping("/movimientos/{productoId}")
    public ResponseEntity<List<MovimientoInventario>> obtenerHistorialMovimientos(
            @PathVariable Integer productoId) {
        
        log.info("Consultando historial de movimientos del producto ID: {}", productoId);
        List<MovimientoInventario> movimientos = 
            inventarioService.obtenerHistorialMovimientos(productoId);
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener productos con stock bajo
     * GET /api/inventario/bajo-stock
     */
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Inventario>> obtenerInventarioBajoStock() {
        log.info("Consultando productos con stock bajo");
        List<Inventario> inventarios = inventarioService.obtenerInventarioBajoStock();
        return ResponseEntity.ok(inventarios);
    }
    
    /**
     * Obtener inventario por almacén
     * GET /api/inventario/almacen/1
     */
    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<List<Inventario>> obtenerInventarioPorAlmacen(
            @PathVariable Integer almacenId) {
        
        log.info("Consultando inventario del almacén ID: {}", almacenId);
        List<Inventario> inventarios = inventarioService.obtenerInventarioPorAlmacen(almacenId);
        return ResponseEntity.ok(inventarios);
    }
    
    /**
     * Obtener stock de un producto en un almacén específico
     * GET /api/inventario/producto/1/almacen/1
     */
    @GetMapping("/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<Map<String, Object>> obtenerStockEnAlmacen(
            @PathVariable Integer productoId,
            @PathVariable Integer almacenId) {
        
        log.info("Consultando stock: producto={}, almacén={}", productoId, almacenId);
        
        Inventario inventario = inventarioService.obtenerStockEnAlmacen(productoId, almacenId);
        
        if (inventario == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("productoId", productoId);
            response.put("almacenId", almacenId);
            response.put("stockActual", 0);
            response.put("stockReservado", 0);
            response.put("stockDisponible", 0);
            response.put("mensaje", "No hay registro de inventario");
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("productoId", productoId);
        response.put("almacenId", almacenId);
        response.put("stockActual", inventario.getStockActual());
        response.put("stockReservado", inventario.getStockReservado());
        response.put("stockDisponible", inventario.getStockDisponible());
        response.put("ubicacion", inventario.getUbicacionEstante());
        response.put("fechaUltimoMovimiento", inventario.getFechaUltimoMovimiento());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reservar stock para una venta
     * POST /api/inventario/reservar?productoId=1&almacenId=1&cantidad=5
     */
    @PostMapping("/reservar")
    public ResponseEntity<Map<String, Object>> reservarStock(
            @RequestParam Integer productoId,
            @RequestParam(required = false, defaultValue = "1") Integer almacenId,
            @RequestParam Integer cantidad) {
        
        log.info("Reservando stock: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        try {
            inventarioService.reservarStock(productoId, almacenId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock reservado exitosamente");
            response.put("productoId", productoId);
            response.put("cantidadReservada", cantidad);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al reservar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Liberar stock reservado
     * POST /api/inventario/liberar?productoId=1&almacenId=1&cantidad=5
     */
    @PostMapping("/liberar")
    public ResponseEntity<Map<String, Object>> liberarStock(
            @RequestParam Integer productoId,
            @RequestParam(required = false, defaultValue = "1") Integer almacenId,
            @RequestParam Integer cantidad) {
        
        log.info("Liberando stock reservado: producto={}, almacén={}, cantidad={}", 
            productoId, almacenId, cantidad);
        
        try {
            inventarioService.liberarStockReservado(productoId, almacenId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock liberado exitosamente");
            response.put("productoId", productoId);
            response.put("cantidadLiberada", cantidad);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al liberar stock", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
