package com.erp.erp.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {
    private Integer idEmpleado;
    private String codigoEmpleado;
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String genero;
    private String rfc;
    private String curp;
    private String nss;
    private String cp;
    private String direccion;
    private String telefono;
    private String correo;
    private LocalDate fechaContratacion;
    private LocalDate fechaTermino;
    private BigDecimal salarioBase;
    private String tipoContrato;
    private String jornada;
    private String estado;
    private String rol;
    
    // Relaciones simplificadas
    private PuestoDTO puesto;
    private DepartamentoDTO departamento;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PuestoDTO {
        private Integer idPuesto;
        private String codigoPuesto;
        private String nombrePuesto;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartamentoDTO {
        private Integer idDepartamento;
        private String codigoDepartamento;
        private String nombreDepartamento;
    }
}