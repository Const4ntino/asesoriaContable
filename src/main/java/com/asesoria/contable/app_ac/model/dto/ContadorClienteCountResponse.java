package com.asesoria.contable.app_ac.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContadorClienteCountResponse {
    private Long idContador;
    private String nombres;
    private String apellidos;
    private Long totalClientes;
}
