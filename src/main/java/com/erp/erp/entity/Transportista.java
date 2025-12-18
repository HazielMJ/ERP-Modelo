package com.erp.erp.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Transportista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transportista")
    private Integer idTransportista;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 100)
    private String empresa;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "tipo_vehiculo", length = 50)
    private String tipoVehiculo;
    
    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;
    
    @Column(length = 50)
    private String licencia;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoTransportista estado = EstadoTransportista.ACTIVO;
    
    public enum EstadoTransportista {
        ACTIVO, INACTIVO
    }
}
