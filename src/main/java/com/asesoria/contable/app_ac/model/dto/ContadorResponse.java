package com.asesoria.contable.app_ac.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContadorResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String email;
    private String especialidad;
    private String nroColegiatura;
    private UsuarioResponse usuario;
    private Long numeroClientes;
}
