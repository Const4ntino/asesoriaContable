package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse;
import com.asesoria.contable.app_ac.model.dto.EstadisticasContadorResponse;
import com.asesoria.contable.app_ac.model.dto.RegimenClienteCountResponse;
import com.asesoria.contable.app_ac.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contadores")
    public EstadisticasContadorResponse getEstadisticasContadores() {
        return estadisticasService.calcularEstadisticasContadores();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contadores/top")
    public List<ContadorClienteCountResponse> getTopContadores(@RequestParam(defaultValue = "5") int limit) {
        return estadisticasService.findTopContadores(limit);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contadores/bottom")
    public List<ContadorClienteCountResponse> getBottomContadores(@RequestParam(defaultValue = "5") int limit) {
        return estadisticasService.findBottomContadores(limit);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/clientes/sin-asignar")
    public long getClientesSinAsignar() {
        return estadisticasService.countClientesSinAsignar();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/clientes/distribucion-regimen")
    public List<RegimenClienteCountResponse> getDistribucionRegimen() {
        return estadisticasService.countClientesByRegimen();
    }
}
