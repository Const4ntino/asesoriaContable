package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.AlertaClienteService;
import com.asesoria.contable.app_ac.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alertas-cliente")
@RequiredArgsConstructor
public class AlertaClienteController {

    private final AlertaClienteService alertaClienteService;
    private final ClienteService clienteService;

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaClienteResponse> findById(@PathVariable Long id) {
        AlertaClienteResponse alerta = alertaClienteService.findById(id);
        return ResponseEntity.ok(alerta);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-alertas")
    public ResponseEntity<List<AlertaClienteResponse>> findAllMyAlerts(@AuthenticationPrincipal Usuario usuario) {
        Cliente cliente = clienteService.findEntityByUsuarioId(usuario.getId());
        List<AlertaClienteResponse> alertas = alertaClienteService.findAllByClienteId(cliente.getId());
        return ResponseEntity.ok(alertas);
    }

//    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
//    @GetMapping
//    public ResponseEntity<List<AlertaClienteResponse>> findAll() {
//        List<AlertaClienteResponse> alertas = alertaClienteService.findAll();
//        return ResponseEntity.ok(alertas);
//    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
    @PostMapping
    public ResponseEntity<AlertaClienteResponse> save(@Valid @RequestBody AlertaClienteRequest request) {
        AlertaClienteResponse newAlerta = alertaClienteService.save(request);
        return new ResponseEntity<>(newAlerta, HttpStatus.CREATED);
    }

//    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
//    @PutMapping("/{id}")
//    public ResponseEntity<AlertaClienteResponse> update(@PathVariable Long id, @Valid @RequestBody AlertaClienteRequest request) {
//        AlertaClienteResponse updatedAlerta = alertaClienteService.update(id, request);
//        return ResponseEntity.ok(updatedAlerta);
//    }

//    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CONTADOR')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        alertaClienteService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }




}
