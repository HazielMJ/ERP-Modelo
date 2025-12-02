package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "apertura_caja", indexes = {
    @Index(name = "idx_apertura_caja", columnList = "id_caja"),
    @Index(name = "idx_apertura_usuario", columnList = "id_usuario"),
    @Index(name = "idx_apertura_fecha", columnList = "fecha_apertura"),
    @Index(name = "idx_apertura_estado", columnList = "estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AperturaCaja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apertura")
    private Integer idApertura;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_caja", nullable = false)
    @NotNull(message = "La caja es requerida")
    private Caja caja;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "El usuario es requerido")
    private Usuario usuario;
    
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo inicial es requerido")
    @DecimalMin(value = "0.0", message = "El saldo inicial debe ser mayor o igual a 0")
    private BigDecimal saldoInicial;
    
    @Builder.Default
    @Column(name = "saldo_final", precision = 15, scale = 2)
    private BigDecimal saldoFinal = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_ingresos", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalIngresos = BigDecimal.ZERO;
    
    @Builder.Default
    @Column(name = "total_egresos", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalEgresos = BigDecimal.ZERO;
    
    // CORREGIDO: saldo_real ahora tiene valor por defecto y no puede ser null
    @Builder.Default
    @Column(name = "saldo_real", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoReal = BigDecimal.ZERO;
    
    // Campo calculado virtualmente
    @Transient
    private BigDecimal saldoEsperado;
    
    // Campo calculado virtualmente
    @Transient
    private BigDecimal diferencia;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false,
            columnDefinition = "ENUM('ABIERTA','CERRADA') DEFAULT 'ABIERTA'")
    private EstadoApertura estado = EstadoApertura.ABIERTA;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Builder.Default
    @OneToMany(mappedBy = "apertura", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<MovimientoCaja> movimientos = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        // CORREGIDO: Asegurar que saldoReal tenga valor al crear
        if (saldoReal == null) {
            saldoReal = saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        // Validar que si está cerrada, debe tener saldo real válido
        if (estado == EstadoApertura.CERRADA && 
            (saldoReal == null || saldoReal.compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalStateException("El saldo real es requerido y debe ser mayor o igual a 0 para cerrar la caja");
        }
    }
    
    // Métodos de cálculo
    @PostLoad
    @PostPersist
    @PostUpdate
    protected void calcularCamposVirtuales() {
        this.saldoEsperado = calcularSaldoEsperado();
        this.diferencia = calcularDiferencia();
    }
    
    public BigDecimal calcularSaldoEsperado() {
        return saldoInicial.add(totalIngresos).subtract(totalEgresos);
    }
    
    public BigDecimal calcularDiferencia() {
        return saldoReal.subtract(calcularSaldoEsperado());
    }
    
    public BigDecimal getSaldoActual() {
        if (estado == EstadoApertura.CERRADA) {
            return saldoFinal;
        }
        return calcularSaldoEsperado();
    }
    
    // Métodos de utilidad
    public void addMovimiento(MovimientoCaja movimiento) {
        movimientos.add(movimiento);
        movimiento.setApertura(this);
        
        // Actualizar totales
        if (movimiento.getTipoMovimiento() == MovimientoCaja.TipoMovimiento.INGRESO) {
            this.totalIngresos = this.totalIngresos.add(movimiento.getMonto());
        } else {
            this.totalEgresos = this.totalEgresos.add(movimiento.getMonto());
        }
    }
    
    public void removeMovimiento(MovimientoCaja movimiento) {
        movimientos.remove(movimiento);
        movimiento.setApertura(null);
        
        // Actualizar totales
        if (movimiento.getTipoMovimiento() == MovimientoCaja.TipoMovimiento.INGRESO) {
            this.totalIngresos = this.totalIngresos.subtract(movimiento.getMonto());
        } else {
            this.totalEgresos = this.totalEgresos.subtract(movimiento.getMonto());
        }
    }
    
    public boolean isAbierta() {
        return estado == EstadoApertura.ABIERTA;
    }
    
    public boolean isCerrada() {
        return estado == EstadoApertura.CERRADA;
    }
    
    public void cerrar(BigDecimal saldoRealFinal, String observacionesCierre) {
        if (isCerrada()) {
            throw new IllegalStateException("La caja ya está cerrada");
        }
        
        if (saldoRealFinal == null || saldoRealFinal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo real final debe ser mayor o igual a 0");
        }
        
        this.saldoReal = saldoRealFinal;
        this.saldoFinal = saldoRealFinal;
        this.fechaCierre = LocalDateTime.now();
        this.estado = EstadoApertura.CERRADA;
        
        if (observacionesCierre != null && !observacionesCierre.isEmpty()) {
            this.observaciones = (this.observaciones != null ? this.observaciones + "\n" : "") 
                               + "CIERRE: " + observacionesCierre;
        }
        
        calcularCamposVirtuales();
    }
    
    public boolean tieneDiferencia() {
        return diferencia != null && diferencia.compareTo(BigDecimal.ZERO) != 0;
    }
    
    public boolean tieneFaltante() {
        return diferencia != null && diferencia.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean tieneSobrante() {
        return diferencia != null && diferencia.compareTo(BigDecimal.ZERO) > 0;
    }
    
    // Constructor personalizado para creación de aperturas
    public static AperturaCaja crearApertura(Caja caja, Usuario usuario, BigDecimal saldoInicial, String observaciones) {
        BigDecimal saldoInicialValido = saldoInicial != null ? saldoInicial : BigDecimal.ZERO;
        
        return AperturaCaja.builder()
                .caja(caja)
                .usuario(usuario)
                .saldoInicial(saldoInicialValido)
                .saldoReal(saldoInicialValido) // ✅ Inicializar saldo_real igual al saldo inicial
                .observaciones(observaciones)
                .fechaApertura(LocalDateTime.now())
                .estado(EstadoApertura.ABIERTA)
                .totalIngresos(BigDecimal.ZERO)
                .totalEgresos(BigDecimal.ZERO)
                .saldoFinal(BigDecimal.ZERO)
                .build();
    }
    
    public enum EstadoApertura {
        ABIERTA, 
        CERRADA
    }
}