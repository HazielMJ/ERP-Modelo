package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "Compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;
    
    @Column(name = "numero_compra", unique = true, nullable = false, length = 50)
    private String numeroCompra;
    
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;
    
    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    @JsonIgnoreProperties({"compras", "hibernateLazyInitializer", "handler"})
    private Proveedor proveedor;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"password", "compras", "ventas", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
    
    @Column(name = "numero_factura_proveedor", length = 50)
    private String numeroFacturaProveedor;
    
    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Builder.Default
    @Column(precision = 15, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_compra", precision = 15, scale = 2)
    private BigDecimal totalCompra = BigDecimal.ZERO;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDIENTE','RECIBIDA','PARCIAL','ANULADA') DEFAULT 'PENDIENTE'")
    private EstadoCompra estado = EstadoCompra.PENDIENTE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", columnDefinition = "ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','CREDITO') DEFAULT 'EFECTIVO'")
    private TipoPago tipoPago = TipoPago.EFECTIVO;
    
    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;
    
    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @JsonManagedReference
    @Builder.Default
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();
    
    public enum EstadoCompra {
        PENDIENTE, RECIBIDA, PARCIAL, ANULADA
    }
    
    public enum TipoPago {
        EFECTIVO, TARJETA, TRANSFERENCIA, CREDITO
    }
}
