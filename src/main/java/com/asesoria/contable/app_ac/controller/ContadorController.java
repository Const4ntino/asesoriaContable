package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.ContadorRequest;
import com.asesoria.contable.app_ac.model.dto.ContadorResponse;
import com.asesoria.contable.app_ac.service.ContadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.asesoria.contable.app_ac.model.dto.ClienteConMetricasResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contadores")
public class ContadorController {

    private final ContadorService contadorService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public List<ContadorResponse> findAll() {
        return contadorService.findAll();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ContadorResponse findById(@PathVariable Long id) {
        return contadorService.findById(id);
    }

    // Proteger esta URI con JWT
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ContadorResponse findByUsuario(@PathVariable Long usuarioId) {
        return contadorService.findByUsuarioId(usuarioId);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<ContadorResponse> save(@Valid @RequestBody ContadorRequest request) {
        ContadorResponse trabajadorResponse = contadorService.save(request);
        return ResponseEntity
                .created(URI.create("/api/contadores/" + trabajadorResponse.getId()))
                .body(trabajadorResponse);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ContadorResponse update(@PathVariable Long id, @Valid @RequestBody ContadorRequest request) {
        return contadorService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contadorService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/search")
    public List<ContadorResponse> searchContadores(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return contadorService.searchContadores(searchTerm, sortBy, sortOrder);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/naturales/metricas")
    public ResponseEntity<List<ClienteConMetricasResponse>> getClientesNaturalesConMetricas(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) String regimen,
            @RequestParam(required = false) String rucDni,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<ClienteConMetricasResponse> clientes = contadorService.getClientesNaturalesConMetricas(usuario, regimen, rucDni, nombres, sortBy, sortOrder);
        return ResponseEntity.ok(clientes);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/juridicos/metricas")
    public ResponseEntity<List<ClienteConMetricasResponse>> getClientesJuridicosConMetricas(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) String regimen,
            @RequestParam(required = false) String rucDni,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<ClienteConMetricasResponse> clientes = contadorService.getClientesJuridicosConMetricas(usuario, regimen, rucDni, nombres, sortBy, sortOrder);
        return ResponseEntity.ok(clientes);
    }


}
