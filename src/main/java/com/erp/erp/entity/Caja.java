package com.erp.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "caja", indexes = {
    @Index(name = "idx_numero_caja", columnList = "numero_caja"),
    @Index(name = "idx_estado_caja", columnList = "estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Integer idCaja;
    
    @Column(name = "numero_caja", unique = true, nullable = false, length = 50)
    @NotBlank(message = "El número de caja es requerido")
    private String numeroCaja;
    
    @Column(name = "nombre_caja", nullable = false, length = 100)
    @NotBlank(message = "El nombre de caja es requerido")
    private String nombreCaja;
    
    @Column(name = "ubicacion", length = 100)
    private String ubicacion;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, 
            columnDefinition = "ENUM('ACTIVA','INACTIVA','MANTENIMIENTO') DEFAULT 'ACTIVA'")
    private EstadoCaja estado = EstadoCaja.ACTIVA;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    // Métodos de utilidad
    public boolean isActiva() {
        return estado == EstadoCaja.ACTIVA;
    }
    
    public boolean isInactiva() {
        return estado == EstadoCaja.INACTIVA;
    }
    
    public boolean isEnMantenimiento() {
        return estado == EstadoCaja.MANTENIMIENTO;
    }
    
    public void activar() {
        this.estado = EstadoCaja.ACTIVA;
    }
    
    public void desactivar() {
        this.estado = EstadoCaja.INACTIVA;
    }
    
    public void ponerEnMantenimiento() {
        this.estado = EstadoCaja.MANTENIMIENTO;
    }
    
    public enum EstadoCaja {
        ACTIVA, 
        INACTIVA, 
        MANTENIMIENTO
    }
}