package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "impuesto")  // ✅ MINÚSCULA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Impuesto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_impuesto")
    @JsonProperty("idImpuesto")
    private Integer idImpuesto;
    
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoImpuesto tipo;
    
    @Column(name = "tasa", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasa;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoImpuesto estado = EstadoImpuesto.ACTIVO;
    
    @Column(name = "fecha_creacion", updatable = false)
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    public enum TipoImpuesto {
        IVA, ISR, IEPS, OTRO
    }
    
    public enum EstadoImpuesto {
        ACTIVO, INACTIVO
    }
}
