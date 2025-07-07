package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse;
import com.asesoria.contable.app_ac.model.dto.EstadisticasContadorResponse;
import com.asesoria.contable.app_ac.model.dto.RegimenClienteCountResponse;
import com.asesoria.contable.app_ac.repository.ClienteRepository;
import com.asesoria.contable.app_ac.repository.ContadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements EstadisticasService {

    private final ContadorRepository contadorRepository;
    private final ClienteRepository clienteRepository;

    public EstadisticasContadorResponse calcularEstadisticasContadores() {
        long totalContadores = contadorRepository.count();
        long totalClientesAsignados = clienteRepository.countByContadorIsNotNull();

        double promedio = (totalContadores == 0) ? 0 : (double) totalClientesAsignados / totalContadores;

        return EstadisticasContadorResponse.builder()
                .totalContadores(totalContadores)
                .totalClientesAsignados(totalClientesAsignados)
                .promedioClientesPorContador(promedio)
                .build();
    }

    @Override
    public List<ContadorClienteCountResponse> findTopContadores(int limit) {
        return contadorRepository.findTopContadoresByClientes(PageRequest.of(0, limit));
    }

    @Override
    public List<ContadorClienteCountResponse> findBottomContadores(int limit) {
        return contadorRepository.findBottomContadoresByClientes(PageRequest.of(0, limit));
    }

    @Override
    public long countClientesSinAsignar() {
        return clienteRepository.countByContadorIsNull();
    }

    @Override
    public List<RegimenClienteCountResponse> countClientesByRegimen() {
        return clienteRepository.countClientesByRegimen();
    }
}
