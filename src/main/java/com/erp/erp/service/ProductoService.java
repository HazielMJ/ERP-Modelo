package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    public Producto crearProducto(Producto producto) {
        if (productoRepository.findByCodigoProducto(producto.getCodigoProducto()).isPresent()) {
            throw new RuntimeException("Ya existe un producto con ese código");
        }
        return productoRepository.save(producto);
    }
    
    public Producto actualizarProducto(Integer id, Producto producto) {
        Producto existente = obtenerProductoPorId(id);
        existente.setNombreProducto(producto.getNombreProducto());
        existente.setDescripcion(producto.getDescripcion());
        existente.setCategoria(producto.getCategoria());
        existente.setMarca(producto.getMarca());
        existente.setModelo(producto.getModelo());
        existente.setPrecioCompra(producto.getPrecioCompra());
        existente.setPrecioVenta(producto.getPrecioVenta());
        existente.setStockMinimo(producto.getStockMinimo());
        existente.setStockMaximo(producto.getStockMaximo());
        return productoRepository.save(existente);
    }
    
    public void descontinuarProducto(Integer id) {
        Producto producto = obtenerProductoPorId(id);
        producto.setEstado(Producto.EstadoProducto.DESCONTINUADO);
        productoRepository.save(producto);
    }
    
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorCodigo(String codigo) {
        return productoRepository.findByCodigoProducto(codigo)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
    
  
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findAllActive();
    }
    
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosBajoStock() {
        return productoRepository.findProductosBajoStock();
    }



@Transactional
public Producto generarCodigoBarras(Integer id) {
    Producto producto = obtenerProductoPorId(id);
    

    String codigoBarras = generarCodigoBarrasTexto(producto);
    producto.setCodigoBarras(codigoBarras);
    
    return productoRepository.save(producto);
}

@Transactional
public List<Producto> generarCodigosBarrasMasivo() {
    List<Producto> productos = productoRepository.findAllActive();
    
    productos.forEach(producto -> {
        if (producto.getCodigoBarras() == null || producto.getCodigoBarras().isEmpty()) {
            String codigoBarras = generarCodigoBarrasTexto(producto);
            producto.setCodigoBarras(codigoBarras);
        }
    });
    
    return productoRepository.saveAll(productos);
}

@Transactional(readOnly = true)
public List<Producto> obtenerProductosSinCodigoBarras() {
    return productoRepository.findAll().stream()
        .filter(p -> p.getCodigoBarras() == null || p.getCodigoBarras().isEmpty())
        .filter(p -> p.getEstado() == Producto.EstadoProducto.ACTIVO)
        .collect(java.util.stream.Collectors.toList());
}


private String generarCodigoBarrasTexto(Producto producto) {
    // Formato: MARCA-NOMBRE-CODIGO
    StringBuilder sb = new StringBuilder();
    
    if (producto.getMarca() != null && !producto.getMarca().isEmpty()) {
        sb.append(producto.getMarca().toUpperCase().replaceAll("[^A-Z0-9]", ""));
        sb.append("-");
    }
    
    String nombreLimpio = producto.getNombreProducto()
            .toUpperCase()
            .replaceAll("[^A-Z0-9]", "");
    

    if (nombreLimpio.length() > 15) {
        nombreLimpio = nombreLimpio.substring(0, 15);
    }
    
    sb.append(nombreLimpio);
    sb.append("-");
    sb.append(producto.getCodigoProducto());
    
    return sb.toString();
}
}
