package com.erp.erp.controller;

import com.erp.erp.entity.*;
import com.erp.erp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {
    private final ProductoService productoService;
    private final InventarioService inventarioService; // ⭐ AGREGAR ESTA DEPENDENCIA
    
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productoService.crearProducto(producto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }
    
    @PutMapping("/{id}/descontinuar")
    public ResponseEntity<Void> descontinuarProducto(@PathVariable Integer id) {
        productoService.descontinuarProducto(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }
    
    // ⭐ NUEVO ENDPOINT: Productos con información de stock
    @GetMapping("/con-stock")
    public ResponseEntity<List<Map<String, Object>>> obtenerProductosConStock() {
        try {
            // Obtener productos activos
            List<Producto> productos = productoService.obtenerProductosActivos();
            
            // Enriquecer cada producto con su stock disponible
            List<Map<String, Object>> productosConStock = productos.stream()
                .map(producto -> {
                    Map<String, Object> productoMap = new HashMap<>();
                    
                    // Datos del producto
                    productoMap.put("idProducto", producto.getIdProducto());
                    productoMap.put("codigoProducto", producto.getCodigoProducto());
                    productoMap.put("codigoBarras", producto.getCodigoBarras());
                    productoMap.put("nombreProducto", producto.getNombreProducto());
                    productoMap.put("descripcion", producto.getDescripcion());
                    productoMap.put("categoria", producto.getCategoria());
                    productoMap.put("marca", producto.getMarca());
                    productoMap.put("modelo", producto.getModelo());
                    productoMap.put("unidadMedida", producto.getUnidadMedida());
                    productoMap.put("precioCompra", producto.getPrecioCompra());
                    productoMap.put("precioVenta", producto.getPrecioVenta());
                    productoMap.put("margenUtilidad", producto.getMargenUtilidad());
                    productoMap.put("stockMinimo", producto.getStockMinimo());
                    productoMap.put("stockMaximo", producto.getStockMaximo());
                    productoMap.put("puntoReorden", producto.getPuntoReorden());
                    productoMap.put("aplicaIva", producto.getAplicaIva());
                    productoMap.put("tasaIva", producto.getTasaIva());
                    productoMap.put("estado", producto.getEstado());
                    
                    // ⭐ STOCK DISPONIBLE (suma de todos los almacenes)
                    Integer stockDisponible = inventarioService.getStockDisponibleByProducto(
                        producto.getIdProducto()
                    );
                    productoMap.put("stockDisponible", stockDisponible != null ? stockDisponible : 0);
                    
                    return productoMap;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(productosConStock);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> obtenerProductoPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.obtenerProductoPorCodigo(codigo));
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        return ResponseEntity.ok(productoService.obtenerProductosActivos());
    }
    
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Producto>> obtenerProductosBajoStock() {
        return ResponseEntity.ok(productoService.obtenerProductosBajoStock());
    }

    // ============ NUEVOS ENDPOINTS PARA CÓDIGOS DE BARRAS ============

@PostMapping("/{id}/generar-codigo-barras")
public ResponseEntity<Producto> generarCodigoBarras(@PathVariable Integer id) {
    try {
        Producto producto = productoService.generarCodigoBarras(id);
        return ResponseEntity.ok(producto);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@PostMapping("/generar-codigos-barras-masivo")
public ResponseEntity<List<Producto>> generarCodigosBarrasMasivo() {
    try {
        List<Producto> productos = productoService.generarCodigosBarrasMasivo();
        return ResponseEntity.ok(productos);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

@GetMapping("/sin-codigo-barras")
public ResponseEntity<List<Producto>> obtenerProductosSinCodigoBarras() {
    return ResponseEntity.ok(productoService.obtenerProductosSinCodigoBarras());
}
}
