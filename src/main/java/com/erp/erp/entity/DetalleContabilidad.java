package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "detalle_contable")  // ✅ MINÚSCULA + SNAKE_CASE
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DetalleContable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    @JsonProperty("idDetalle")
    private Integer idDetalle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asiento", nullable = false)
    @JsonIgnoreProperties({"detalles", "hibernateLazyInitializer", "handler"})
    private Contabilidad asiento;
    
    @Column(name = "numero_linea", nullable = false)
    @JsonProperty("numeroLinea")
    private Integer numeroLinea;
    
    @Column(name = "cuenta_contable", nullable = false, length = 50)
    @JsonProperty("cuentaContable")
    private String cuentaContable;
    
    @Column(name = "descripcion_linea", length = 255)
    @JsonProperty("descripcionLinea")
    private String descripcionLinea;
    
    @Builder.Default
    @Column(name = "debe", precision = 15, scale = 2)
    private BigDecimal debe = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "haber", precision = 15, scale = 2)
    private BigDecimal haber = BigDecimal.ZERO;
    
    // ✅ Helper para obtener solo el ID del asiento (evita lazy loading)
    @JsonProperty("idAsiento")
    public Integer getIdAsiento() {
        return asiento != null ? asiento.getIdAsiento() : null;
    }
}
