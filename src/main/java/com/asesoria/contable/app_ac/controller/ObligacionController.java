package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;
import com.asesoria.contable.app_ac.service.ObligacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obligaciones")
@RequiredArgsConstructor
public class ObligacionController {

    private final ObligacionService obligacionService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ObligacionResponse> findById(@PathVariable Long id) {
        ObligacionResponse obligacion = obligacionService.findById(id);
        return ResponseEntity.ok(obligacion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<ObligacionResponse>> findAll() {
        List<ObligacionResponse> obligaciones = obligacionService.findAll();
        return ResponseEntity.ok(obligaciones);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<ObligacionResponse> save(@Valid @RequestBody ObligacionRequest request) {
        ObligacionResponse newObligacion = obligacionService.save(request);
        return new ResponseEntity<>(newObligacion, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/from-declaracion")
    public ResponseEntity<ObligacionResponse> saveFromDeclaracion(@Valid @RequestBody DeclaracionRequest request) {
        ObligacionResponse newObligacion = obligacionService.saveFromDeclaracion(request);
        return new ResponseEntity<>(newObligacion, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ObligacionResponse> update(@PathVariable Long id, @Valid @RequestBody ObligacionRequest request) {
        ObligacionResponse updatedObligacion = obligacionService.update(id, request);
        return ResponseEntity.ok(updatedObligacion);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        obligacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CONTADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ObligacionResponse>> getObligacionesByClienteId(@PathVariable Long clienteId) {
        List<ObligacionResponse> obligaciones = obligacionService.findByClienteId(clienteId);
        return ResponseEntity.ok(obligaciones);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @GetMapping("/mis-clientes/ultimas-obligaciones")
    public ResponseEntity<List<ObligacionResponse>> getLatestObligacionesForMyClients(@AuthenticationPrincipal Usuario usuario) {
        List<ObligacionResponse> obligaciones = obligacionService.getLatestObligacionesForMyClients(usuario);
        return ResponseEntity.ok(obligaciones);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-obligaciones")
    public ResponseEntity<List<ObligacionResponse>> getMisObligaciones(@AuthenticationPrincipal Usuario usuario) {
        List<ObligacionResponse> obligaciones = obligacionService.buscarMisObligaciones(usuario);
        return ResponseEntity.ok(obligaciones);
    }
}
