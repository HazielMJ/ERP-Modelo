package com.erp.erp.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilDTO {
    private Integer id;
    private String nombre;
    private String email;
    private Boolean activo;
    private Boolean cambiarPassword;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime logout;
    
    // Información del empleado asociado
    private Integer idEmpleado;
    private String codigoEmpleado;
    private String nombreEmpleado;
    private String rol;
    private String departamento;
    private String puesto;
}