package com.erp.erp.service;

import com.erp.erp.entity.*;
import com.erp.erp.exception.ResourceNotFoundException;
import com.erp.erp.repository.DetalleVentaRepository;
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
public class DetalleVentaService {
    
    private final DetalleVentaRepository detalleVentaRepository;
    
    @Transactional(readOnly = true)
    public List<DetalleVenta> obtenerDetallesPorVenta(Integer ventaId) {
        log.info("Obteniendo detalles de venta: {}", ventaId);
        return detalleVentaRepository.findByVentaId(ventaId);
    }
    

    @Transactional(readOnly = true)
    public DetalleVenta obtenerDetallePorId(Integer id) {
        return detalleVentaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Detalle de venta no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ProductoVendidoDTO> obtenerProductosMasVendidos(
            LocalDateTime inicio, LocalDateTime fin) {
        log.info("Obteniendo productos más vendidos desde {} hasta {}", inicio, fin);
        
        List<Object[]> resultados = detalleVentaRepository.findProductosMasVendidos(inicio, fin);
        
        return resultados.stream()
            .map(result -> {
                Producto producto = (Producto) result[0];
                BigDecimal cantidad = (BigDecimal) result[1];
                Long numeroVentas = (Long) result[2];
                BigDecimal totalIngresos = (BigDecimal) result[3];
                
                return new ProductoVendidoDTO(
                    producto.getIdProducto(),
                    producto.getCodigoProducto(),
                    producto.getNombreProducto(),
                    cantidad,
                    numeroVentas,
                    totalIngresos
                );
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public EstadisticasProductoDTO obtenerEstadisticasProducto(
            Integer productoId, LocalDateTime inicio, LocalDateTime fin) {
        log.info("Obteniendo estadísticas del producto: {}", productoId);
        
        BigDecimal unidadesVendidas = detalleVentaRepository
            .getTotalUnidadesVendidasPorProducto(productoId, inicio, fin);
        
        BigDecimal ingresos = detalleVentaRepository
            .getTotalIngresosPorProducto(productoId, inicio, fin);
        
        Long numeroVentas = detalleVentaRepository.countVentasByProducto(productoId);
        
        return new EstadisticasProductoDTO(
            productoId,
            unidadesVendidas,
            numeroVentas,
            ingresos
        );
    }
    

    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalUnidadesVendidas(LocalDateTime inicio, LocalDateTime fin) {
        return detalleVentaRepository.getTotalUnidadesVendidas(inicio, fin);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalDescuentos(LocalDateTime inicio, LocalDateTime fin) {
        return detalleVentaRepository.getTotalDescuentosAplicados(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<VentasPorCategoriaDTO> obtenerVentasPorCategoria(
            LocalDateTime inicio, LocalDateTime fin) {
        log.info("Obteniendo ventas por categoría");
        
        List<Object[]> resultados = detalleVentaRepository.findVentasPorCategoria(inicio, fin);
        
        return resultados.stream()
            .map(result -> {
                Categoria categoria = (Categoria) result[0];
                Long productosDistintos = (Long) result[1];
                BigDecimal cantidadTotal = (BigDecimal) result[2];
                BigDecimal totalVentas = (BigDecimal) result[3];
                
                return new VentasPorCategoriaDTO(
                    categoria != null ? categoria.getIdCategoria() : null,
                    categoria != null ? categoria.getNombreCategoria() : "Sin categoría",
                    productosDistintos,
                    cantidadTotal,
                    totalVentas
                );
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<DetalleVenta> obtenerDetallesVentasHoy() {
        return detalleVentaRepository.findDetallesVentasHoy();
    }
    
    // ==================== DTOs ====================
    
    public record ProductoVendidoDTO(
        Integer idProducto,
        String codigoProducto,
        String nombreProducto,
        BigDecimal cantidadVendida,
        Long numeroVentas,
        BigDecimal totalIngresos
    ) {}
    
    public record EstadisticasProductoDTO(
        Integer idProducto,
        BigDecimal unidadesVendidas,
        Long numeroVentas,
        BigDecimal ingresos
    ) {}
    
    public record VentasPorCategoriaDTO(
        Integer idCategoria,
        String nombreCategoria,
        Long productosDistintos,
        BigDecimal cantidadTotal,
        BigDecimal totalVentas
    ) {}
}