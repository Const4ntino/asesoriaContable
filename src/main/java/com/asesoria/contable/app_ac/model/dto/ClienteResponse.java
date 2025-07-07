package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoCliente;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String rucDni;
    private String email;
    private String telefono;
    private String tipoRuc;
    private Regimen regimen;
    private TipoCliente tipoCliente;
    private UsuarioResponse usuario;
    private ContadorResponse contador;
}
