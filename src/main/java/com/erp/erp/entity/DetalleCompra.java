package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "Detalle_Compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;
    
    // ⭐ CAMBIO PRINCIPAL: Agregamos @JsonBackReference
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;
    
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad = BigDecimal.ONE;
    
    @Builder.Default
    @Column(name = "precio_unitario", precision = 15, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "cantidad_recibida", precision = 10, scale = 2)
    private BigDecimal cantidadRecibida = BigDecimal.ZERO;
    
}
