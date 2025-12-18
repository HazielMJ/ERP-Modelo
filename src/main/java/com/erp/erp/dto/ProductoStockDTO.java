package com.erp.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoStockDTO {
    private Integer idProducto;
    private String codigoProducto;
    private String codigoBarras;
    private String nombreProducto;
    private String descripcion;
    private String marca;
    private String modelo;
    private String unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal margenUtilidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer puntoReorden;
    private Boolean aplicaIva;
    private BigDecimal tasaIva;
    private String estado;
    private Integer categoriaId;
    private String categoriaNombre;
    private Integer stockDisponible;
}









