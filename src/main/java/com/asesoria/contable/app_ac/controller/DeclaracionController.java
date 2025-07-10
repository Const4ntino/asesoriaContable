package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.dto.PeriodoVencimientoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.DeclaracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import com.asesoria.contable.app_ac.utils.enums.EstadoContador;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/declaraciones")
@RequiredArgsConstructor
public class DeclaracionController {

    private final DeclaracionService declaracionService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<DeclaracionResponse> findById(@PathVariable Long id) {
        DeclaracionResponse declaracion = declaracionService.findById(id);
        return ResponseEntity.ok(declaracion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<DeclaracionResponse>> findAll() {
        List<DeclaracionResponse> declaraciones = declaracionService.findAll();
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<DeclaracionResponse> save(@Valid @RequestBody DeclaracionRequest request) {
        DeclaracionResponse newDeclaracion = declaracionService.save(request);
        return new ResponseEntity<>(newDeclaracion, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DeclaracionResponse> update(@PathVariable Long id, @Valid @RequestBody DeclaracionRequest request) {
        DeclaracionResponse updatedDeclaracion = declaracionService.update(id, request);
        return ResponseEntity.ok(updatedDeclaracion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        declaracionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADOR')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<DeclaracionResponse>> getDeclaracionesByClienteId(@PathVariable Long clienteId) {
        List<DeclaracionResponse> declaraciones = declaracionService.findByClienteId(clienteId);
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/actual")
    public ResponseEntity<DeclaracionResponse> generarDeclaracionSiNoExiste(@AuthenticationPrincipal Usuario usuario) {
        DeclaracionResponse declaracion = declaracionService.generarDeclaracionSiNoExiste(usuario);
        return ResponseEntity.ok(declaracion);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-declaraciones")
    public ResponseEntity<List<DeclaracionResponse>> getMisDeclaraciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) DeclaracionEstado estado,
            @RequestParam(required = false) EstadoContador estadoContador) {

        List<DeclaracionResponse> declaraciones = declaracionService.buscarMisDeclaraciones(
                usuario, fechaInicio, fechaFin, estado, estadoContador);

        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PatchMapping("/{id}/notificar-contador")
    public ResponseEntity<DeclaracionResponse> notificarContador(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        DeclaracionResponse declaracionActualizada = declaracionService.notificarContador(id, usuario);
        return ResponseEntity.ok(declaracionActualizada);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-declaraciones/primera-creada")
    public ResponseEntity<DeclaracionResponse> getPrimeraDeclaracionCreada(@AuthenticationPrincipal Usuario usuario) {
        DeclaracionResponse declaracion = declaracionService.findFirstCreadaByUsuario(usuario);
        return ResponseEntity.ok(declaracion);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-declaraciones/vencimiento-actual")
    public ResponseEntity<PeriodoVencimientoResponse> getPeriodoActualYFechaVencimiento(@AuthenticationPrincipal Usuario usuario) {
        PeriodoVencimientoResponse response = declaracionService.getPeriodoActualYFechaVencimiento(usuario);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/ultimas-declaraciones")
    public ResponseEntity<List<DeclaracionResponse>> getLatestDeclarationsForMyClients(@AuthenticationPrincipal Usuario usuario) {
        List<DeclaracionResponse> declaraciones = declaracionService.getLatestDeclarationsForMyClients(usuario);
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/ultimas-declaraciones/filtrar")
    public ResponseEntity<List<DeclaracionResponse>> searchLatestDeclarationsForMyClients(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) String nombresCliente,
            @RequestParam(required = false) String regimenCliente,
            @RequestParam(required = false) String rucDniCliente,
            @RequestParam(required = false) Integer periodoTributarioMes,
            @RequestParam(required = false) BigDecimal totalPagarDeclaracion,
            @RequestParam(required = false) DeclaracionEstado estado) {

        List<DeclaracionResponse> declaraciones = declaracionService.searchLatestDeclarationsForMyClients(
                usuario,
                nombresCliente,
                regimenCliente,
                rucDniCliente,
                periodoTributarioMes,
                totalPagarDeclaracion,
                estado);
        return ResponseEntity.ok(declaraciones);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @PatchMapping("/{id}/marcar-en-proceso")
    public ResponseEntity<DeclaracionResponse> marcarComoEnProceso(@PathVariable Long id) {
        DeclaracionResponse declaracionActualizada = declaracionService.marcarComoEnProceso(id);
        return ResponseEntity.ok(declaracionActualizada);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @PatchMapping("/{id}/marcar-declarado")
    public ResponseEntity<DeclaracionResponse> marcarComoDeclaradoYGenerarObligacion(@PathVariable Long id) {
        DeclaracionResponse declaracionActualizada = declaracionService.marcarComoDeclaradoYGenerarObligacion(id);
        return ResponseEntity.ok(declaracionActualizada);
    }

}