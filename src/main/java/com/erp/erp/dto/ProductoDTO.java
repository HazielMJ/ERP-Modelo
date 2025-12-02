ackage com.erp.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Integer idProducto;
    private String codigoProducto;
    private String codigoBarras;
    private String nombreProducto;
    private String descripcion;
    private String marca;
    private String modelo;
    private String unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private String estado;
    private String categoriaNombre;
    private Integer categoriaId;
    
    // Información adicional para el código de barras
    private String textoCodigoBarras; // Texto que se usa para generar el código
}
