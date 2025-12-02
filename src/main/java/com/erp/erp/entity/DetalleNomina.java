package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalle_nomina")  // ✅ MINÚSCULA + SNAKE_CASE
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DetalleNomina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_nomina")
    @JsonProperty("idDetalleNomina")
    private Integer idDetalleNomina;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nomina", nullable = false)
    @JsonIgnoreProperties({"detalles", "hibernateLazyInitializer", "handler"})
    private Nomina nomina;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoConcepto tipo;
    
    @Column(name = "concepto", nullable = false, length = 100)
    private String concepto;
    
    @Column(name = "clave_sat", length = 10)
    @JsonProperty("claveSat")
    private String claveSat;
    
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;
    
    // ✅ Helper para obtener solo el ID de la nómina
    @JsonProperty("idNomina")
    public Integer getIdNomina() {
        return nomina != null ? nomina.getIdNomina() : null;
    }
    
    public enum TipoConcepto {
        PERCEPCION, DEDUCCION
    }
}
