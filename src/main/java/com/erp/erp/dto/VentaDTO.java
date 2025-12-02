package com.erp.erp.dto;

import com.erp.erp.entity.Venta;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDTO {
    
    private Integer idVenta;
    
    private String numeroVenta;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaVenta;
    
    @NotNull(message = "El cliente es requerido")
    private ClienteSimpleDTO cliente;
    
    @NotNull(message = "El usuario es requerido")
    private UsuarioSimpleDTO usuario;
    
    @DecimalMin(value = "0.0", message = "El subtotal debe ser mayor o igual a 0")
    private BigDecimal subtotal;
    
    @DecimalMin(value = "0.0", message = "Los impuestos deben ser mayor o igual a 0")
    private BigDecimal impuestos;
    
    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    private BigDecimal descuento;
    
    @DecimalMin(value = "0.0", message = "El total debe ser mayor o igual a 0")
    private BigDecimal totalVenta;
    
    @NotNull(message = "El estado es requerido")
    private Venta.EstadoVenta estado;
    
    @NotNull(message = "El tipo de pago es requerido")
    private Venta.TipoPago tipoPago;
    
    @DecimalMin(value = "0.0", message = "El monto recibido debe ser mayor o igual a 0")
    private BigDecimal montoRecibido;
    
    @DecimalMin(value = "0.0", message = "El cambio debe ser mayor o igual a 0")
    private BigDecimal cambio;
    
    private String observaciones;
    
    @Valid
    @NotEmpty(message = "Debe agregar al menos un producto")
    @Builder.Default
    private List<DetalleVentaDTO> detalles = new ArrayList<>();
    
    // DTOs internos simples
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteSimpleDTO {
        @NotNull
        private Integer idCliente;
        private String nombreRazonSocial;
        private String rfc;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioSimpleDTO {
        @NotNull
        private Integer id;
        private String nombre;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleVentaDTO {
        private Integer idDetalle;
        
        @NotNull(message = "El producto es requerido")
        private ProductoSimpleDTO producto;
        
        @NotNull(message = "La cantidad es requerida")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
        private BigDecimal cantidad;
        
        @NotNull(message = "El precio unitario es requerido")
        @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
        private BigDecimal precioUnitario;
        
        @Builder.Default
        private BigDecimal descuento = BigDecimal.ZERO;
        
        private BigDecimal subtotal;
        private BigDecimal impuesto;
        private BigDecimal total;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoSimpleDTO {
        @NotNull
        private Integer idProducto;
        private String codigoProducto;
        private String nombreProducto;
        private BigDecimal precioVenta;
    }
}
