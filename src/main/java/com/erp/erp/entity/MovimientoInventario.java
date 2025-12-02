package com.erp.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.*;


@Entity
@Table(name = "Movimiento_Inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;
    
    @Column(name = "fecha_movimiento")
    private LocalDateTime fechaMovimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "almacen_origen_id")
    private Almacen almacenOrigen;
    
    @ManyToOne
    @JoinColumn(name = "almacen_destino_id")
    private Almacen almacenDestino;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;
    
    @Column(name = "costo_unitario", precision = 12, scale = 2)
    private BigDecimal costoUnitario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "referencia_tipo")
    private TipoReferencia referenciaTipo;
    
    @Column(name = "referencia_id")
    private Integer referenciaId;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
    
    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE, TRANSFERENCIA
    }
    
    public enum TipoReferencia {
        COMPRA, VENTA, AJUSTE, TRANSFERENCIA
    }
}
