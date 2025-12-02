package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "factura")  // ✅ MINÚSCULA (tu tabla se llama "factura", no "facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    @JsonProperty("idFactura")
    private Integer idFactura;
    
    @Column(name = "numero_factura", unique = true, nullable = false, length = 50)
    @JsonProperty("numeroFactura")
    private String numeroFactura;
    
    @Column(name = "serie", length = 10)
    private String serie;
    
    @Column(name = "folio")
    private Integer folio;
    
    @Column(name = "fecha_emision", nullable = false)
    @JsonProperty("fechaEmision")
    private LocalDate fechaEmision;
    
    @Column(name = "fecha_vencimiento", nullable = false)
    @JsonProperty("fechaVencimiento")
    private LocalDate fechaVencimiento;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Venta venta;
    
    @Builder.Default
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "impuestos", precision = 15, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "descuentos", precision = 15, scale = 2)
    private BigDecimal descuentos = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('PENDIENTE', 'PAGADA', 'VENCIDA', 'ANULADA') DEFAULT 'PENDIENTE'")
    private EstadoFactura estado = EstadoFactura.PENDIENTE;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_factura", columnDefinition = "ENUM('VENTA', 'COMPRA') DEFAULT 'VENTA'")
    @JsonProperty("tipoFactura")
    private TipoFactura tipoFactura = TipoFactura.VENTA;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", columnDefinition = "ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') DEFAULT 'EFECTIVO'")
    @JsonProperty("formaPago")
    private FormaPago formaPago = FormaPago.EFECTIVO;
    
    @Column(name = "uso_cfdi", length = 10)
    @JsonProperty("usoCfdi")
    private String usoCfdi;
    
    @Column(name = "metodo_pago", length = 10)
    @JsonProperty("metodoPago")
    private String metodoPago;
    
    @Column(name = "uuid_fiscal", length = 100)
    @JsonProperty("uuidFiscal")
    private String uuidFiscal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
    
    @Column(name = "fecha_creacion", updatable = false)
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    // ✅ Helpers para IDs
    @JsonProperty("idCliente")
    public Integer getIdCliente() {
        return cliente != null ? cliente.getIdCliente() : null;
    }
    
    @JsonProperty("idVenta")
    public Integer getIdVenta() {
        return venta != null ? venta.getIdVenta() : null;
    }
    
    public enum EstadoFactura {
        PENDIENTE, PAGADA, VENCIDA, ANULADA
    }
    
    public enum TipoFactura {
        VENTA, COMPRA
    }
    
    public enum FormaPago {
        EFECTIVO, TARJETA, TRANSFERENCIA, MIXTO
    }
}
