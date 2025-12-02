package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "contabilidad")  // ✅ MINÚSCULA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contabilidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asiento")
    @JsonProperty("idAsiento")
    private Integer idAsiento;
    
    @Column(name = "fecha_asiento", nullable = false)
    @JsonProperty("fechaAsiento")
    private LocalDate fechaAsiento;
    
    @Column(name = "numero_asiento", unique = true, nullable = false, length = 50)
    @JsonProperty("numeroAsiento")
    private String numeroAsiento;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('BORRADOR', 'CONTABILIZADO', 'ANULADO') DEFAULT 'BORRADOR'")
    private EstadoAsiento estado = EstadoAsiento.BORRADOR;
    
    @ManyToOne(fetch = FetchType.EAGER)  // ✅ EAGER para que cargue el usuario
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;
    
    @Builder.Default
    @Column(name = "total_debe", precision = 15, scale = 2)
    @JsonProperty("totalDebe")
    private BigDecimal totalDebe = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_haber", precision = 15, scale = 2)
    @JsonProperty("totalHaber")
    private BigDecimal totalHaber = BigDecimal.ZERO;
    
    @Column(name = "periodo_contable", length = 10)
    @JsonProperty("periodoContable")
    private String periodoContable;
    
    @Column(name = "fecha_contabilizacion")
    @JsonProperty("fechaContabilizacion")
    private LocalDateTime fechaContabilizacion;
    
    @Column(name = "fecha_creacion", updatable = false)
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @Builder.Default
    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("asiento")
    private List<DetalleContable> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    public enum EstadoAsiento {
        BORRADOR, CONTABILIZADO, ANULADO
    }
}
