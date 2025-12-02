package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;
    
    @Column(name = "codigo_categoria", unique = true, nullable = false, length = 50)
    private String codigoCategoria;
    
    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    // ⭐ SOLUCIÓN: Ignorar completamente la referencia padre en JSON
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVA','INACTIVA') DEFAULT 'ACTIVA'")
    private EstadoCategoria estado = EstadoCategoria.ACTIVA;
    
    public enum EstadoCategoria {
        ACTIVA, INACTIVA
    }
}
