package com.erp.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "Envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Integer idEnvio;
    
    @Column(name = "numero_guia", unique = true, nullable = false, length = 100)
    private String numeroGuia;
    
    @ManyToOne
    @JoinColumn(name = "id_venta")
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_metodo_envio")
    private MetodoEnvio metodoEnvio;
    
    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;
    
    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;
    
    @Column(name = "direccion_entrega", nullable = false, columnDefinition = "TEXT")
    private String direccionEntrega;
    
    @Column(length = 100)
    private String ciudad;
    
    @Column(name = "estado_destino", length = 100)
    private String estadoDestino;
    
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;
    
    @Column(name = "referencia_direccion", columnDefinition = "TEXT")
    private String referenciaDireccion;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_estimada_entrega")
    private LocalDate fechaEstimadaEntrega;
    
    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDIENTE','PREPARANDO','EN_TRANSITO','ENTREGADO','CANCELADO','DEVUELTO') DEFAULT 'PENDIENTE'")
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;
    
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;
    
    @Column(name = "peso_kg", precision = 10, scale = 2)
    private BigDecimal pesoKg;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Builder.Default
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeguimientoEnvio> seguimientos = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    public enum EstadoEnvio {
        PENDIENTE, PREPARANDO, EN_TRANSITO, ENTREGADO, CANCELADO, DEVUELTO
    }
}
