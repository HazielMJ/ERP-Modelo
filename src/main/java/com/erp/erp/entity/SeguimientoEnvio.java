package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Seguimiento_Envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Integer idSeguimiento;
    
    // ✅ Evita ciclos con Envio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    @JsonIgnoreProperties({"seguimientos", "venta", "hibernateLazyInitializer", "handler"})
    @ToString.Exclude
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
    @Column(name = "estado_envio", length = 20)
    private EstadoSeguimiento estadoEnvio;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    // ✅ Evita ciclos con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;
    
    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }
    
    public enum EstadoSeguimiento {
        PENDIENTE,
        PREPARANDO,
        EN_TRANSITO,
        ENTREGADO,
        CANCELADO,
        DEVUELTO,
        RETRASADO,
        INCIDENTE
    }
}
