package com.asesoria.contable.app_ac.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteInfoDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String rucDni;
    private String tipoRuc;
    private String regimen;
    private String tipoCliente;
    private String email;
    private String telefono;
}
