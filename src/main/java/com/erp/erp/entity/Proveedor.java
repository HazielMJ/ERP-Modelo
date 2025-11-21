package com.erp.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;
    
    @Column(name = "nombre_empresa", nullable = false, length = 200)
    private String nombreEmpresa;
    
    @Column(length = 13)
    private String rfc;
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Column(length = 50)
    private String telefono;
    
    @Column(length = 150)
    private String email;
    
    @Column(name = "contacto_nombre", length = 150)
    private String contactoNombre;
    
    @Column(name = "contacto_telefono", length = 50)
    private String contactoTelefono;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoProveedor estado = EstadoProveedor.ACTIVO;

    @Builder.Default
    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
    
    public enum EstadoProveedor {
        ACTIVO, INACTIVO
    }
}
