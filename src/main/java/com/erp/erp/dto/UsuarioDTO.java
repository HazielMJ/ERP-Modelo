package com.erp.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String nombre;
    private String email;
    private Boolean activo;
    private Boolean cambiarPassword;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}