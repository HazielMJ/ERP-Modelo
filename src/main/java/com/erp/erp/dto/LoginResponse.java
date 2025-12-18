package com.erp.erp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer id;
    private String nombre;
    private String email;
    private Boolean activo;
    private Boolean cambiarPassword;
    private String mensaje;
    
    // ✅ NUEVOS CAMPOS: Información del rol y empleado
    private String rol;                // ROL del empleado: ADMIN, GERENTE, VENDEDOR, etc.
    private Integer idEmpleado;        // ID del empleado vinculado
    private String nombreEmpleado;     // Nombre completo del empleado
    private String codigoEmpleado;     // Código del empleado
}