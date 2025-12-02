package com.erp.erp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarPerfilRequest {
    private String nombre;
    private String email;
    private Boolean activo;
    private Boolean cambiarPassword;
    
    // Campos opcionales para cambio de contraseña
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
