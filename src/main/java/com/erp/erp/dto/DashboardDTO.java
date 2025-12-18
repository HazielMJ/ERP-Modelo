package com.erp.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para retornar datos completos del Dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    
    // Estadísticas principales
    private EstadisticasDTO estadisticas;
    
    // Resumen financiero
    private ResumenFinancieroDTO resumenFinanciero;
    
    // Últimas ventas
    private List<VentaResumeDTO> ultimasVentas;
    
    // Alertas del sistema
    private AlertasDTO alertas;
    
    // Distribución de ventas por categoría
    private List<DistribucionVentasDTO> distribucionVentas;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasDTO {
        private BigDecimal ventasMes;
        private Double porcentajeVentas;
        private Integer pedidosTotales;
        private Double porcentajePedidos;
        private Integer productosStock;
        private Double porcentajeStock;
        private Integer clientesActivos;
        private Double porcentajeClientes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenFinancieroDTO {
        private BigDecimal ingresosMes;
        private BigDecimal egresosMes;
        private BigDecimal gananciaNeta;
        private Double porcentajeMeta;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaResumeDTO {
        private String numeroVenta;
        private String cliente;
        private String tiempoTranscurrido;
        private BigDecimal total;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertasDTO {
        private Integer stockBajo;
        private Integer facturasVencidas;
        private String proximaNomina;
        private String ultimoRespaldo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionVentasDTO {
        private String categoria;
        private String icono;
        private Double porcentaje;
    }
}