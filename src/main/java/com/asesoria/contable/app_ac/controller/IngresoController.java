package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.ClienteService;
import com.asesoria.contable.app_ac.service.EgresoService;
import com.asesoria.contable.app_ac.service.IngresoService;
import com.asesoria.contable.app_ac.utils.enums.Regimen;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;
    private final ClienteService clienteService;
    private final EgresoService egresoService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<IngresoResponse> findById(@PathVariable Long id) {
        IngresoResponse ingreso = ingresoService.findById(id);
        return ResponseEntity.ok(ingreso);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<IngresoResponse>> findAll() {
        List<IngresoResponse> ingresos = ingresoService.findAll();
        return ResponseEntity.ok(ingresos);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<IngresoResponse> save(@Valid @RequestBody IngresoRequest request) {
        IngresoResponse newIngreso = ingresoService.save(request);
        return new ResponseEntity<>(newIngreso, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<IngresoResponse> update(@PathVariable Long id, @Valid @RequestBody IngresoRequest request) {
        IngresoResponse updatedIngreso = ingresoService.update(id, request);
        return ResponseEntity.ok(updatedIngreso);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingresoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/mis-ingresos/nuevo")
    public ResponseEntity<IngresoResponse> saveMyIngreso(@Valid @RequestBody IngresoRequest request,
                                                         @AuthenticationPrincipal Usuario usuario) {
        IngresoResponse newIngreso = ingresoService.saveByUsuario(request, usuario);
        return new ResponseEntity<>(newIngreso, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/mis-ingresos/{id}")
    public ResponseEntity<IngresoResponse> updateMyIngreso(@PathVariable Long id,
                                                           @Valid @RequestBody IngresoRequest request,
                                                           @AuthenticationPrincipal Usuario usuario) {
        IngresoResponse updated = ingresoService.updateMyIngreso(id, request, usuario);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/mis-ingresos/{id}")
    public ResponseEntity<Void> deleteMyIngreso(@PathVariable Long id,
                                                @AuthenticationPrincipal Usuario usuario) {
        ingresoService.deleteMyIngreso(id, usuario);
        return ResponseEntity.noContent().build();
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-ingresos")
    public ResponseEntity<List<IngresoResponse>> getMyIngresos(@AuthenticationPrincipal Usuario usuario) {
        List<IngresoResponse> ingresos = ingresoService.findByUsuarioId(usuario.getId());
        return ResponseEntity.ok(ingresos);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-ingresos/metricas")
    public Map<String, Object> obtenerMetricasIngresos(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        System.out.println(cliente.toString());

        // Solo proceder si el cliente es NRUS
        if (cliente.getRegimen() != Regimen.NRUS) {
            throw new AccessDeniedException("Endpoint solo disponible para clientes NRUS");
        }

        Map<String, Object> metricas = new HashMap<>();

        // Total mes actual
        BigDecimal totalMesActual = ingresoService.calcularTotalMesActual(cliente.getId());
        metricas.put("totalMesActual", totalMesActual);

        // Total mes anterior
        BigDecimal totalMesAnterior = ingresoService.calcularTotalMesAnterior(cliente.getId());
        metricas.put("totalMesAnterior", totalMesAnterior);

        // Variación porcentual
        BigDecimal variacion = BigDecimal.ZERO;
        if (totalMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
            variacion = totalMesActual.subtract(totalMesAnterior)
                    .divide(totalMesAnterior, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }
        metricas.put("variacionPorcentual", variacion);

        // Contador de comprobantes
        Long cantidadComprobantes = ingresoService.contarComprobantesMesActual(cliente.getId());
        metricas.put("cantidadComprobantes", cantidadComprobantes);

        return metricas;
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-ingresos/metricas-avanzadas")
    public Map<String, Object> obtenerMetricasIngresosNoNrus(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());

        // Solo permitir si el cliente NO es NRUS
        if (cliente.getRegimen() == Regimen.NRUS) {
            throw new AccessDeniedException("Endpoint no disponible para clientes NRUS");
        }

        Map<String, Object> metricas = new HashMap<>();

        // Total ingresos del mes actual
        BigDecimal totalMesActual = ingresoService.calcularTotalMesActual(cliente.getId());
        metricas.put("totalMesActual", totalMesActual);

        // Desglose por tipo tributario
        Map<String, BigDecimal> ingresosPorTipoTributario = ingresoService.obtenerIngresosPorTipoTributario(cliente.getId());
        metricas.put("ingresosPorTipoTributario", ingresosPorTipoTributario);

        // Ingresos recurrentes
        List<Map<String, Object>> ingresosRecurrentes = ingresoService.identificarIngresosRecurrentes(cliente.getId());
        metricas.put("ingresosRecurrentes", ingresosRecurrentes);

        // Total egresos (para balance)
        BigDecimal totalEgresosMesActual = egresoService.calcularTotalMesActual(cliente.getId());
        BigDecimal balanceMensual = totalMesActual.subtract(totalEgresosMesActual);
        metricas.put("balanceMensual", balanceMensual);

        return metricas;
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<IngresoResponse>> getIngresosByClienteId(@PathVariable Long clienteId) {
        List<IngresoResponse> ingresos = ingresoService.findByClienteId(clienteId);
        return ResponseEntity.ok(ingresos);
    }

    // Filtrar por rango de fechas
    @GetMapping("/cliente/{clienteId}/fecha")
    public ResponseEntity<List<IngresoResponse>> getIngresosByClienteIdAndFechaBetween(
            @PathVariable Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<IngresoResponse> ingresos = ingresoService.findByClienteIdAndFechaBetween(clienteId, startDate, endDate);
        return ResponseEntity.ok(ingresos);
    }

}
