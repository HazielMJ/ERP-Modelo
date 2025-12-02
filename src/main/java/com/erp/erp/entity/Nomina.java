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
@Table(name = "nomina")  // ✅ MINÚSCULA
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Nomina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nomina")
    @JsonProperty("idNomina")
    private Integer idNomina;
    
    @ManyToOne(fetch = FetchType.EAGER)  // ✅ EAGER para cargar empleado
    @JoinColumn(name = "id_empleado", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "puesto", "departamento"})
    private Empleado empleado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asiento_contable")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Contabilidad asientoContable;
    
    @Column(name = "periodo_nomina", length = 50)
    @JsonProperty("periodoNomina")
    private String periodoNomina;
    
    @Column(name = "fecha_inicio")
    @JsonProperty("fechaInicio")
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin")
    @JsonProperty("fechaFin")
    private LocalDate fechaFin;
    
    @Column(name = "fecha_pago")
    @JsonProperty("fechaPago")
    private LocalDate fechaPago;
    
    @Column(name = "dias_trabajados")
    @JsonProperty("diasTrabajados")
    private Integer diasTrabajados;
    
    @Column(name = "salario_diario", precision = 12, scale = 2)
    @JsonProperty("salarioDiario")
    private BigDecimal salarioDiario;
    
    @Builder.Default
    @Column(name = "total_percepciones", precision = 12, scale = 2)
    @JsonProperty("totalPercepciones")
    private BigDecimal totalPercepciones = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_deducciones", precision = 12, scale = 2)
    @JsonProperty("totalDeducciones")
    private BigDecimal totalDeducciones = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_neto", precision = 12, scale = 2)
    @JsonProperty("totalNeto")
    private BigDecimal totalNeto = BigDecimal.ZERO;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_nomina", columnDefinition = "ENUM('MENSUAL','QUINCENAL','SEMANAL') DEFAULT 'QUINCENAL'")
    @JsonProperty("tipoNomina")
    private TipoNomina tipoNomina = TipoNomina.QUINCENAL;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('PENDIENTE','PAGADA','ANULADA') DEFAULT 'PENDIENTE'")
    private EstadoNomina estado = EstadoNomina.PENDIENTE;
    
    @Column(name = "fecha_creacion", updatable = false)
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @Builder.Default
    @OneToMany(mappedBy = "nomina", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("nomina")
    private List<DetalleNomina> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    // ✅ Helper para obtener solo el ID del empleado
    @JsonProperty("idEmpleado")
    public Integer getIdEmpleado() {
        return empleado != null ? empleado.getIdEmpleado() : null;
    }
    
    public enum TipoNomina {
        MENSUAL, QUINCENAL, SEMANAL
    }
    
    public enum EstadoNomina {
        PENDIENTE, PAGADA, ANULADA
    }
}
