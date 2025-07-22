package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.ClienteService;
import com.asesoria.contable.app_ac.service.EgresoService;
import com.asesoria.contable.app_ac.service.IngresoService;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/egresos")
@RequiredArgsConstructor
public class EgresoController {

    private final EgresoService egresoService;
    private final ClienteService clienteService;
    private final IngresoService ingresoService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<EgresoResponse> getEgresoById(@PathVariable Long id) {
        EgresoResponse egreso = egresoService.findById(id);
        return ResponseEntity.ok(egreso);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<EgresoResponse>> getAllEgresos() {
        List<EgresoResponse> egresos = egresoService.findAll();
        return ResponseEntity.ok(egresos);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<EgresoResponse> createEgreso(@Valid @RequestBody EgresoRequest request) {
        EgresoResponse newEgreso = egresoService.save(request);
        return new ResponseEntity<>(newEgreso, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EgresoResponse> updateEgreso(@PathVariable Long id, @Valid @RequestBody EgresoRequest request) {
        EgresoResponse updatedEgreso = egresoService.update(id, request);
        return ResponseEntity.ok(updatedEgreso);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEgreso(@PathVariable Long id) {
        egresoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EgresoResponse>> getEgresosByCliente(@PathVariable Long clienteId) {
        List<EgresoResponse> egresos = egresoService.findByClienteId(clienteId);
        return ResponseEntity.ok(egresos);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADOR')")
    @GetMapping("/cliente/{clienteId}/filtrar")
    public ResponseEntity<Page<EgresoResponse>> filtrarEgresos(
            @PathVariable Long clienteId,
            @RequestParam(required = false) BigDecimal montoMinimo,
            @RequestParam(required = false) BigDecimal montoMaximo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) TipoTributario tipoTributario,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String nroComprobante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<EgresoResponse> egresos = egresoService.filtrarEgresos(
                clienteId,
                montoMinimo,
                montoMaximo,
                fechaInicio,
                fechaFin,
                mes,
                anio,
                tipoTributario,
                descripcion,
                nroComprobante,
                pageable);

        return ResponseEntity.ok(egresos);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/mis-egresos/nuevo")
    public ResponseEntity<EgresoResponse> saveMyEgreso(@Valid @RequestBody EgresoRequest request,
                                                       @AuthenticationPrincipal Usuario usuario) {
        EgresoResponse newIngreso = egresoService.saveByUsuario(request, usuario);
        return new ResponseEntity<>(newIngreso, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/mis-egresos/{id}")
    public ResponseEntity<EgresoResponse> updateMyEgreso(@PathVariable Long id,
                                                         @Valid @RequestBody EgresoRequest request,
                                                         @AuthenticationPrincipal Usuario usuario) {
        EgresoResponse updated = egresoService.updateMyEgreso(id, request, usuario);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/mis-egresos/{id}")
    public ResponseEntity<Void> deleteMyEgreso(@PathVariable Long id,
                                               @AuthenticationPrincipal Usuario usuario) {
        egresoService.deleteMyEgreso(id, usuario);
        return ResponseEntity.noContent().build();
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-egresos")
    public ResponseEntity<List<EgresoResponse>> getMyEgresos(@AuthenticationPrincipal Usuario usuario) {
        List<EgresoResponse> ingresos = egresoService.findByUsuarioId(usuario.getId());
        return ResponseEntity.ok(ingresos);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-egresos/metricas")
    public Map<String, Object> obtenerMetricasEgresos(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());

        // Solo proceder si el cliente es NRUS
        if (cliente.getRegimen() != Regimen.NRUS) {
            throw new AccessDeniedException("Endpoint solo disponible para clientes NRUS");
        }

        Map<String, Object> metricas = new HashMap<>();

        // Total mes actual
        BigDecimal totalMesActual = egresoService.calcularTotalMesActual(cliente.getId());
        metricas.put("totalMesActual", totalMesActual);

        // Total mes anterior
        BigDecimal totalMesAnterior = egresoService.calcularTotalMesAnterior(cliente.getId());
        metricas.put("totalMesAnterior", totalMesAnterior);

        // Desglose por tipo de contabilidad
        Map<String, BigDecimal> egresosPorTipoContabilidad = egresoService.obtenerEgresosPorTipoContabilidad(cliente.getId());
        metricas.put("egresosPorTipoContabilidad", egresosPorTipoContabilidad);

        // Balance mensual (requiere datos de ingresos)
        BigDecimal totalIngresosMesActual = ingresoService.calcularTotalMesActual(cliente.getId());
        BigDecimal balanceMensual = totalIngresosMesActual.subtract(totalMesActual);
        metricas.put("balanceMensual", balanceMensual);

        // Egresos recurrentes
        List<Map<String, Object>> egresosRecurrentes = egresoService.identificarEgresosRecurrentes(cliente.getId());
        metricas.put("egresosRecurrentes", egresosRecurrentes);

        return metricas;
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-egresos/metricas-avanzadas")
    public Map<String, Object> obtenerMetricasEgresosNoNrus(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());

        // Solo permitir si el cliente NO es NRUS
        if (cliente.getRegimen() == Regimen.NRUS) {
            throw new AccessDeniedException("Endpoint no disponible para clientes NRUS");
        }

        Map<String, Object> metricas = new HashMap<>();

        // Total de egresos del mes actual
        BigDecimal totalMesActual = egresoService.calcularTotalMesActual(cliente.getId());
        metricas.put("totalMesActual", totalMesActual);

        // Total mes anterior
        BigDecimal totalMesAnterior = egresoService.calcularTotalMesAnterior(cliente.getId());
        metricas.put("totalMesAnterior", totalMesAnterior);

        // Desglose por tipo de contabilidad (COSTO, GASTO)
        Map<String, BigDecimal> egresosPorTipoContabilidad = egresoService.obtenerEgresosPorTipoContabilidad(cliente.getId());
        metricas.put("egresosPorTipoContabilidad", egresosPorTipoContabilidad);

        // Desglose por tipo de tributario (GRAVADA, EXONERADA, INAFECTA)
        Map<String, BigDecimal> egresosPorTipoTributario = egresoService.obtenerEgresosPorTipoTributario(cliente.getId());
        metricas.put("egresosPorTipoTributario", egresosPorTipoTributario);

        // Balance mensual (ingresos - egresos)
        BigDecimal totalIngresosMesActual = ingresoService.calcularTotalMesActual(cliente.getId());
        BigDecimal balanceMensual = totalIngresosMesActual.subtract(totalMesActual);
        metricas.put("balanceMensual", balanceMensual);

        // Egresos recurrentes (por descripción repetida)
        List<Map<String, Object>> egresosRecurrentes = egresoService.identificarEgresosRecurrentes(cliente.getId());
        metricas.put("egresosRecurrentes", egresosRecurrentes);

        return metricas;
    }
}
