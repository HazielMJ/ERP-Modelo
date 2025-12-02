package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "movimiento_caja", indexes = {
    @Index(name = "idx_movimiento_apertura", columnList = "id_apertura"),
    @Index(name = "idx_movimiento_tipo", columnList = "tipo_movimiento"),
    @Index(name = "idx_movimiento_fecha", columnList = "fecha_movimiento"),
    @Index(name = "idx_movimiento_venta", columnList = "id_venta")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoCaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apertura", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @NotNull(message = "La apertura de caja es requerida")
    private AperturaCaja apertura;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false,
            columnDefinition = "ENUM('INGRESO','EGRESO')")
    @NotNull(message = "El tipo de movimiento es requerido")
    private TipoMovimiento tipoMovimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false,
            columnDefinition = "ENUM('VENTA','COMPRA','GASTO','RETIRO','DEPOSITO','AJUSTE','OTRO')")
    @NotNull(message = "La categoría es requerida")
    private CategoriaMovimiento categoria;
    
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
    
    @Column(name = "concepto", nullable = false, length = 200)
    @NotBlank(message = "El concepto es requerido")
    private String concepto;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "El usuario es requerido")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @ToString.Exclude
    private Venta venta;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false,
            columnDefinition = "ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') DEFAULT 'EFECTIVO'")
    private FormaPago formaPago = FormaPago.EFECTIVO;
    
    @Column(name = "referencia", length = 100)
    private String referencia;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;
    
    @PrePersist
    protected void onCreate() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Validar que el monto sea positivo
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El monto debe ser mayor a 0");
        }
        
        // Validar que la apertura esté abierta
        if (apertura != null && !apertura.isAbierta()) {
            throw new IllegalStateException("No se pueden registrar movimientos en una caja cerrada");
        }
    }
    
    // Métodos de utilidad
    public boolean isIngreso() {
        return tipoMovimiento == TipoMovimiento.INGRESO;
    }
    
    public boolean isEgreso() {
        return tipoMovimiento == TipoMovimiento.EGRESO;
    }
    
    public boolean isVenta() {
        return categoria == CategoriaMovimiento.VENTA;
    }
    
    public boolean isRetiro() {
        return categoria == CategoriaMovimiento.RETIRO;
    }
    
    public boolean isGasto() {
        return categoria == CategoriaMovimiento.GASTO;
    }
    
    public boolean isEfectivo() {
        return formaPago == FormaPago.EFECTIVO;
    }
    
    public String getDescripcionCompleta() {
        StringBuilder desc = new StringBuilder();
        desc.append(tipoMovimiento.name()).append(" - ");
        desc.append(categoria.name()).append(": ");
        desc.append(concepto);
        if (venta != null) {
            desc.append(" (Venta #").append(venta.getNumeroVenta()).append(")");
        }
        return desc.toString();
    }
    
    public enum TipoMovimiento {
        INGRESO, 
        EGRESO
    }
    
    public enum CategoriaMovimiento {
        VENTA("Venta"),
        COMPRA("Compra"),
        GASTO("Gasto"),
        RETIRO("Retiro"),
        DEPOSITO("Depósito"),
        AJUSTE("Ajuste"),
        OTRO("Otro");
        
        private final String descripcion;
        
        CategoriaMovimiento(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum FormaPago {
        EFECTIVO("Efectivo"),
        TARJETA("Tarjeta"),
        TRANSFERENCIA("Transferencia"),
        MIXTO("Mixto");
        
        private final String descripcion;
        
        FormaPago(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
}