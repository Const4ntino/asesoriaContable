package com.asesoria.contable.app_ac.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank   
    @Size(min = 0, max = 72)
    private String password;

    @Size(max = 50)
    private String nombres;

    @Size(max = 50)
    private String apellidos;

    @NotBlank
    private String rol;

    private Boolean estado;
}
