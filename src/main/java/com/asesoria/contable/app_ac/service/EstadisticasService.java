package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse;
import com.asesoria.contable.app_ac.model.dto.EstadisticasContadorResponse;
import com.asesoria.contable.app_ac.model.dto.RegimenClienteCountResponse;

import java.util.List;

public interface EstadisticasService {
    EstadisticasContadorResponse calcularEstadisticasContadores();
    List<ContadorClienteCountResponse> findTopContadores(int limit);
    List<ContadorClienteCountResponse> findBottomContadores(int limit);
    long countClientesSinAsignar();
    List<RegimenClienteCountResponse> countClientesByRegimen();
}
