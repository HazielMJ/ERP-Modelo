package com.erp.erp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Almacen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Integer idAlmacen;
    
    @Column(name = "codigo_almacen", nullable = false, unique = true, length = 50)
    private String codigoAlmacen;
    
    @Column(name = "nombre_almacen", nullable = false, length = 100)
    private String nombreAlmacen;
    
    @Column(length = 150)
    private String ubicacion;
    
    @Column(name = "capacidad_total")
    private Integer capacidadTotal;

    @Builder.Default
    @Column(name = "espacio_utilizado")
    private Integer espacioUtilizado = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoAlmacen estado = EstadoAlmacen.ACTIVO;
    
    public enum EstadoAlmacen {
        ACTIVO, INACTIVO
    }
}
