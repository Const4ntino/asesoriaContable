package com.asesoria.contable.app_ac.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String nombres;
    private String apellidos;
    private String rol;
    private boolean estado;
}
