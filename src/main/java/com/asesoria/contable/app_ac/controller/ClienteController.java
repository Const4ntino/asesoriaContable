package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.ClienteMetricasParaContadorResponse;
import com.asesoria.contable.app_ac.model.dto.ClienteRequest;
import com.asesoria.contable.app_ac.model.dto.ClienteResponse;
import com.asesoria.contable.app_ac.model.dto.MetricasDeclaracionResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<ClienteResponse> findAll() {
        return clienteService.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ClienteResponse findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    // Proteger esta URI con JWT
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ClienteResponse findByUsuario(@PathVariable Long usuarioId) {
        return clienteService.findByUsuarioId(usuarioId);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contador/{contadorId}")
    public List<ClienteResponse> findAllByContador(@PathVariable Long contadorId) {
        return clienteService.findAllByContadorId(contadorId);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse clienteResponse = clienteService.save(request);
        return ResponseEntity
                .created(URI.create("/api/clientes/" + clienteResponse.getId()))
                .body(clienteResponse);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ClienteResponse update(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clienteService.deleteById(id);
    }

    // Proteger esta URI con JWT
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/encontrarme")
    public ClienteResponse findByUsuario(@AuthenticationPrincipal Usuario usuario) {
        return clienteService.findByUsuarioId(usuario.getId());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{clienteId}/asignar-contador/{contadorId}")
    public ResponseEntity<ClienteResponse> asignarContador(@PathVariable Long clienteId, @PathVariable Long contadorId) {
        ClienteResponse clienteResponse = clienteService.asignarContador(clienteId, contadorId);
        return ResponseEntity.ok(clienteResponse);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{clienteId}/desasignar-contador")
    public ResponseEntity<Void> desasignarContador(@PathVariable Long clienteId) {
        clienteService.desasignarContador(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/search")
    public List<ClienteResponse> searchClientes(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) String regimen,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return clienteService.searchClientes(searchTerm, tipoCliente, regimen, sortBy, sortOrder);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping("/metricas-declaracion/{id}")
    public ResponseEntity<MetricasDeclaracionResponse> getMetricasDeclaracion(@PathVariable Long id) {
        MetricasDeclaracionResponse metricas = clienteService.getMetricasDeclaracion(id);
        return ResponseEntity.ok(metricas);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/metricas/ingresos-egresos/actual-pasado")
    public ResponseEntity<ClienteMetricasParaContadorResponse> getIngresosEgresosMetricas(@RequestParam Long clienteId) {
        ClienteMetricasParaContadorResponse metricas = clienteService.getIngresosEgresosMetricas(clienteId);
        return ResponseEntity.ok(metricas);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/ingresos/suma")
    public ResponseEntity<BigDecimal> getSumaIngresosCliente(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        BigDecimal sumaIngresos = clienteService.sumIngresosByClienteIdAndPeriodo(cliente.getId(), periodo);
        return ResponseEntity.ok(sumaIngresos);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/egresos/suma")
    public ResponseEntity<BigDecimal> getSumaEgresosCliente(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodo) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        BigDecimal sumaEgresos = clienteService.sumEgresosByClienteIdAndPeriodo(cliente.getId(), periodo);
        return ResponseEntity.ok(sumaEgresos);
    }
}
