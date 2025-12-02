package com.erp.erp.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "MetodoEnvio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_envio")
    private Integer idMetodoEnvio;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "costo_base", precision = 10, scale = 2)
    private BigDecimal costoBase;
    
    @Column(name = "costo_por_km", precision = 10, scale = 2)
    private BigDecimal costoPorKm;
    
    @Column(name = "tiempo_estimado", length = 50)
    private String tiempoEstimado;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoMetodo estado = EstadoMetodo.ACTIVO;
    
    public enum EstadoMetodo {
        ACTIVO, INACTIVO
    }
}
