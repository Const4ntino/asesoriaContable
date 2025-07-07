package com.asesoria.contable.app_ac.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClienteRequest {

    @NotBlank
    @Size(max = 50)
    private String nombres;

    @Size(max = 50)
    private String apellidos;

    @NotBlank
    @Size(min = 8, max = 11)
    private String rucDni;

    @Email
    @Size(max = 100)
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

    private Long idUsuario;

    private Long idContador;
}
