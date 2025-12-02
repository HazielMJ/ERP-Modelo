package com.erp.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    
    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;
    
    @Column(name = "codigo_barras", length = 100)
    private String codigoBarras;
    
    @Column(name = "nombre_producto", nullable = false, length = 150)
    private String nombreProducto;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties({"productos", "hibernateLazyInitializer", "handler"})
    private Categoria categoria;
    
    @Column(length = 100)
    private String marca;
    
    @Column(length = 100)
    private String modelo;
    
    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;
    
    @Column(name = "precio_compra", precision = 12, scale = 2)
    private BigDecimal precioCompra;
    
    @Column(name = "precio_venta", precision = 12, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(name = "margen_utilidad", precision = 5, scale = 2)
    private BigDecimal margenUtilidad;

    @Builder.Default
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;
    
    @Builder.Default
    @Column(name = "stock_maximo")
    private Integer stockMaximo = 0;
    
    @Builder.Default
    @Column(name = "punto_reorden")
    private Integer puntoReorden = 0;
    
    @Builder.Default
    @Column(name = "aplica_iva")
    private Boolean aplicaIva = true;
    
    @Builder.Default
    @Column(name = "tasa_iva", precision = 5, scale = 2)
    private BigDecimal tasaIva = new BigDecimal("16.00");
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVO','INACTIVO','DESCONTINUADO') DEFAULT 'ACTIVO'")
    private EstadoProducto estado = EstadoProducto.ACTIVO;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    public enum EstadoProducto {
        ACTIVO, INACTIVO, DESCONTINUADO
    }
}
