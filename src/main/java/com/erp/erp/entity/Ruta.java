package com.erp.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "Ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;
    
    @Column(name = "codigo_ruta", unique = true, length = 50)
    private String codigoRuta;
    
    @Column(name = "nombre_ruta", length = 100)
    private String nombreRuta;
    
    @Column(length = 100)
    private String origen;
    
    @Column(length = 100)
    private String destino;
    
    @Column(name = "distancia_km", precision = 10, scale = 2)
    private BigDecimal distanciaKm;
    
    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;
    
    @Column(name = "costo_estimado", precision = 10, scale = 2)
    private BigDecimal costoEstimado;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVA','INACTIVA') DEFAULT 'ACTIVA'")
    private EstadoRuta estado = EstadoRuta.ACTIVA;
    
    @Column(columnDefinition = "TEXT")
    private String referencias;
    
    public enum EstadoRuta {
        ACTIVA, INACTIVA
    }
}
