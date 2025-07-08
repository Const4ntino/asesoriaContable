package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.IngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

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
