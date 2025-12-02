package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalle_venta", indexes = {
    @Index(name = "idx_detalle_venta", columnList = "id_venta"),
    @Index(name = "idx_detalle_producto", columnList = "id_producto")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    @JsonBackReference // Evita recursión infinita con Venta
    @ToString.Exclude // Evita stack overflow en logs
    @NotNull(message = "La venta es requerida")
    private Venta venta;
    
    @ManyToOne(fetch = FetchType.EAGER) // EAGER porque siempre necesitamos info del producto
    @JoinColumn(name = "id_producto", nullable = false)
    @NotNull(message = "El producto es requerido")
    private Producto producto;
    
    @Builder.Default
    @Column(name = "cantidad", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad = BigDecimal.ONE;
    
    @Builder.Default
    @Column(name = "precio_unitario", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "El precio unitario es requerido")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "descuento", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El subtotal debe ser mayor o igual a 0")
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "impuesto", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El impuesto debe ser mayor o igual a 0")
    private BigDecimal impuesto = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El total debe ser mayor o igual a 0")
    private BigDecimal total = BigDecimal.ZERO;
    

    public void calcularTotales() {
        // Subtotal = (cantidad * precio) - descuento
        this.subtotal = this.precioUnitario
            .multiply(this.cantidad)
            .subtract(this.descuento);
        
        // Impuesto = subtotal * 0.16 (IVA 16%)
        BigDecimal tasaIva = new BigDecimal("0.16");
        this.impuesto = this.subtotal.multiply(tasaIva);
        
        // Total = subtotal + impuesto
        this.total = this.subtotal.add(this.impuesto);
    }
    

    @PrePersist
    @PreUpdate
    protected void validate() {
        BigDecimal subtotalSinDescuento = this.precioUnitario.multiply(this.cantidad);
        
        if (this.descuento.compareTo(subtotalSinDescuento) > 0) {
            throw new IllegalStateException(
                "El descuento no puede ser mayor al subtotal: " + subtotalSinDescuento
            );
        }
        
        // Recalcular totales antes de guardar
        calcularTotales();
    }

    public BigDecimal getSubtotalSinDescuento() {
        return this.precioUnitario.multiply(this.cantidad);
    }

    public BigDecimal getPorcentajeDescuento() {
        BigDecimal subtotalSinDesc = getSubtotalSinDescuento();
        if (subtotalSinDesc.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.descuento
            .divide(subtotalSinDesc, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }
}