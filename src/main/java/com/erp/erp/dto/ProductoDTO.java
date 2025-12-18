package com.erp.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Integer idProducto;

    @NotBlank(message = "El código es obligatorio")
    private String codigoProducto;

    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombreProducto;

    private String descripcion;
    private String marca;
    private String modelo;
    private String unidadMedida;

    @DecimalMin(value = "0.0", message = "El precio de compra debe ser >= 0")
    private BigDecimal precioCompra;

    @DecimalMin(value = "0.0", message = "El precio de venta debe ser >= 0")
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

    // Información adicional para el código de barras
    private String textoCodigoBarras; // Texto que se usa para generar el código
}