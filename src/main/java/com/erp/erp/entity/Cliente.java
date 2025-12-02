package com.erp.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", columnDefinition = "ENUM('NATURAL', 'JURIDICA') DEFAULT 'NATURAL'")
    private TipoCliente tipoCliente = TipoCliente.NATURAL;
    
    @Column(name = "nombre_razon_social", nullable = false, length = 200)
    private String nombreRazonSocial;
    
    @Column(length = 13)
    private String rfc;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 150)
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO'")
    private EstadoCliente estado = EstadoCliente.ACTIVO;
    
    @Builder.Default
    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
    
    public enum TipoCliente {
        NATURAL, JURIDICA
    }
    
    public enum EstadoCliente {
        ACTIVO, INACTIVO
    }
}
