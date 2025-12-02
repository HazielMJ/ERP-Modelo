package com.erp.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "SeguimientoEnvio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Integer idSeguimiento;
    
    @ManyToOne
    @JoinColumn(name = "id_envio", nullable = false)
    private Envio envio;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @Column(name = "ubicacion_actual", length = 150)
    private String ubicacionActual;
    
    @Column(precision = 10, scale = 6)
    private BigDecimal latitud;
    
    @Column(precision = 10, scale = 6)
    private BigDecimal longitud;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio")
    private EstadoSeguimiento estadoEnvio;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }
    
    public enum EstadoSeguimiento {
        PENDIENTE, PREPARANDO, EN_TRANSITO, ENTREGADO, RETRASADO, INCIDENTE
    }
}
