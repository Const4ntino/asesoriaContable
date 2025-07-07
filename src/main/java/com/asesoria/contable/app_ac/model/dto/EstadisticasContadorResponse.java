package com.asesoria.contable.app_ac.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadisticasContadorResponse {
    private long totalContadores;
    private long totalClientesAsignados;
    private double promedioClientesPorContador;
}
