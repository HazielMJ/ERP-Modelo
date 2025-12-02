package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departamento") 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  
public class Departamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Integer idDepartamento;
    
    @Column(name = "codigo_departamento", unique = true, nullable = false, length = 50)
    private String codigoDepartamento;
    
    @Column(name = "nombre_departamento", nullable = false, length = 100)
    private String nombreDepartamento;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "departamento_padre_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Departamento departamentoPadre;
    
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "id_jefe")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "departamento", "puesto"})
    private Empleado jefe;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoDepartamento estado = EstadoDepartamento.ACTIVO;
    
    public enum EstadoDepartamento {
        ACTIVO, INACTIVO
    }
}
