package com.erp.erp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Tipo_Comprobante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoComprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_comprobante")
    private Integer idTipoComprobante;
    
    @Column(unique = true, nullable = false, length = 10)
    private String codigo;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Builder.Default
    @Column(name = "requiere_serie")
    private Boolean requiereSerie = false;

    @Builder.Default
    @Column(name = "requiere_folio")
    private Boolean requiereFolio = false;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoComprobante estado = EstadoComprobante.ACTIVO;
    
    public enum EstadoComprobante {
        ACTIVO, INACTIVO
    }
}
