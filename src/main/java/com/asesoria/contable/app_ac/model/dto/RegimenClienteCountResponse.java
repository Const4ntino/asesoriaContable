package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.Regimen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenClienteCountResponse {
    private Regimen regimen;
    private Long totalClientes;
}
