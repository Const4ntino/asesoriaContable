package com.asesoria.contable.app_ac.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContadorRequest {

    @NotBlank
    @Size(max = 50)
    private String nombres;

    @NotBlank
    @Size(max = 50)
    private String apellidos;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dni;

    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefono;

    @Size(max = 100)
    private String especialidad;

    @Size(max = 50)
    private String nroColegiatura;

    private Long idUsuario;
}
