package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import lombok.*;

@Entity
@Table(name = "venta", indexes = {
    @Index(name = "idx_numero_venta", columnList = "numero_venta"),
    @Index(name = "idx_fecha_venta", columnList = "fecha_venta"),
    @Index(name = "idx_cliente", columnList = "id_cliente"),
    @Index(name = "idx_estado", columnList = "estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @Column(name = "numero_venta", unique = true, nullable = false, length = 50)
    @NotBlank(message = "El número de venta es requerido")
    private String numeroVenta;
    
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;
    
    // ✅ Evita ciclos con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "El cliente es requerido")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;
    
    // ✅ Evita ciclos con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "El usuario es requerido")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @Builder.Default
    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El subtotal debe ser mayor o igual a 0")
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "impuestos", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Los impuestos deben ser mayor o igual a 0")
    private BigDecimal impuestos = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "descuento", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_venta", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El total debe ser mayor o igual a 0")
    private BigDecimal totalVenta = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('PENDIENTE','PAGADA','ANULADA') DEFAULT 'PENDIENTE'")
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, columnDefinition = "ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','CREDITO','MIXTO') DEFAULT 'EFECTIVO'")
    private TipoPago tipoPago = TipoPago.EFECTIVO;

    @Builder.Default
    @Column(name = "monto_recibido", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El monto recibido debe ser mayor o igual a 0")
    private BigDecimal montoRecibido = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "cambio", precision = 15, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "El cambio debe ser mayor o igual a 0")
    private BigDecimal cambio = BigDecimal.ZERO;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    // ✅ Mantiene detalles para serializar
    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    // ✅ Evita ciclos con Factura
    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private Factura factura;
    
    @PrePersist
    protected void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (estado == EstadoVenta.PAGADA && montoRecibido.compareTo(totalVenta) < 0) {
            throw new IllegalStateException("El monto recibido debe ser mayor o igual al total");
        }
    }
    
    // Métodos de utilidad
    public void addDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
    
    public void removeDetalle(DetalleVenta detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
    }
    
    public void calcularTotales() {
        this.subtotal = detalles.stream()
            .map(DetalleVenta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.impuestos = detalles.stream()
            .map(DetalleVenta::getImpuesto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalVenta = subtotal.add(impuestos).subtract(descuento);
    }
    
    public boolean isPagada() {
        return estado == EstadoVenta.PAGADA;
    }
    
    public boolean isAnulada() {
        return estado == EstadoVenta.ANULADA;
    }
    
    public boolean isPendiente() {
        return estado == EstadoVenta.PENDIENTE;
    }
    
    public enum EstadoVenta {
        PENDIENTE, 
        PAGADA, 
        ANULADA
    }
    
    public enum TipoPago {
        EFECTIVO, 
        TARJETA, 
        TRANSFERENCIA, 
        CREDITO, 
        MIXTO
    }
}