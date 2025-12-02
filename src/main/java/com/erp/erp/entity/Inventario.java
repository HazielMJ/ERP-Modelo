package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer idInventario;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties({"categoria", "detallesCompra", "detallesVenta", "movimientosInventario", "hibernateLazyInitializer", "handler"})
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    @JsonIgnoreProperties({"inventarios", "hibernateLazyInitializer", "handler"})
    private Almacen almacen;
    
    @Builder.Default
    @Column(name = "stock_actual")
    private Integer stockActual = 0;
    
    @Builder.Default
    @Column(name = "stock_reservado")
    private Integer stockReservado = 0;
    
    @Column(name = "stock_disponible", insertable = false, updatable = false)
    private Integer stockDisponible;
    
    @Column(name = "ubicacion_estante", length = 100)
    private String ubicacionEstante;
    
    @Column(name = "fecha_ultimo_movimiento")
    private LocalDateTime fechaUltimoMovimiento;
}
