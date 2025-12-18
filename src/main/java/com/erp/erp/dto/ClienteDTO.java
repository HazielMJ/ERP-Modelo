package com.erp.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Integer idCliente;

    @NotBlank(message = "El tipo de cliente es obligatorio")
    private String tipoCliente;

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(max = 200, message = "El nombre o razón social no debe exceder 200 caracteres")
    private String nombreRazonSocial;

    @Size(max = 13, message = "El RFC no debe exceder 13 caracteres")
    private String rfc;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String telefono;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 150, message = "El correo electrónico no debe exceder 150 caracteres")
    private String email;

    private String direccion;

    private String estado;

    private BigDecimal limiteCredito;

    private BigDecimal saldoActual;

    private String observaciones;

    private LocalDateTime fechaRegistro;

}

