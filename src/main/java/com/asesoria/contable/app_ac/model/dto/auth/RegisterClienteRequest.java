package com.asesoria.contable.app_ac.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterClienteRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 72)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String nombres;

    @NotBlank
    @Size(max = 50)
    private String apellidos;

    @NotBlank
    @Size(min = 8, max = 11)
    private String rucDni;

    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefono;

    @Size(max = 50)
    private String tipoRuc;

    @Size(max = 50)
    private String regimen;

    @NotBlank
    private String tipoCliente;
}
