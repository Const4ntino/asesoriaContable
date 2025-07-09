package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorResponse;
import com.asesoria.contable.app_ac.service.AlertaContadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alertas-contador")
@RequiredArgsConstructor
public class AlertaContadorController {

    private final AlertaContadorService alertaContadorService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaContadorResponse> findById(@PathVariable Long id) {
        AlertaContadorResponse alerta = alertaContadorService.findById(id);
        return ResponseEntity.ok(alerta);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping
    public ResponseEntity<List<AlertaContadorResponse>> findAll() {
        List<AlertaContadorResponse> alertas = alertaContadorService.findAll();
        return ResponseEntity.ok(alertas);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PostMapping
    public ResponseEntity<AlertaContadorResponse> save(@Valid @RequestBody AlertaContadorRequest request) {
        AlertaContadorResponse newAlerta = alertaContadorService.save(request);
        return new ResponseEntity<>(newAlerta, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AlertaContadorResponse> update(@PathVariable Long id, @Valid @RequestBody AlertaContadorRequest request) {
        AlertaContadorResponse updatedAlerta = alertaContadorService.update(id, request);
        return ResponseEntity.ok(updatedAlerta);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertaContadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
