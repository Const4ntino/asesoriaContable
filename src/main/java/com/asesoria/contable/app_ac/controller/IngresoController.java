package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.service.IngresoService;
import com.asesoria.contable.app_ac.utils.enums.TipoTributario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

    @GetMapping("/{id}")
    public ResponseEntity<IngresoResponse> getIngresoById(@PathVariable Long id) {
        IngresoResponse ingreso = ingresoService.findById(id);
        return ResponseEntity.ok(ingreso);
    }

    @GetMapping
    public ResponseEntity<List<IngresoResponse>> getAllIngresos() {
        List<IngresoResponse> ingresos = ingresoService.findAll();
        return ResponseEntity.ok(ingresos);
    }

    @PostMapping
    public ResponseEntity<IngresoResponse> createIngreso(@Valid @RequestBody IngresoRequest request) {
        IngresoResponse newIngreso = ingresoService.save(request);
        return new ResponseEntity<>(newIngreso, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoResponse> updateIngreso(@PathVariable Long id, @Valid @RequestBody IngresoRequest request) {
        IngresoResponse updatedIngreso = ingresoService.update(id, request);
        return ResponseEntity.ok(updatedIngreso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngreso(@PathVariable Long id) {
        ingresoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<IngresoResponse>> getIngresosByCliente(@PathVariable Long idCliente) {
        List<IngresoResponse> ingresos = ingresoService.findByClienteId(idCliente);
        return ResponseEntity.ok(ingresos);
    }

    // Filtrar ingresos por cliente
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-ingresos")
    public ResponseEntity<List<IngresoResponse>> getIngresosByCliente(@AuthenticationPrincipal UserDetails usuario) {
        List<IngresoResponse> ingresos = ingresoService.findAllByCliente(usuario);
        return ResponseEntity.ok(ingresos);
    }

    // Filtrar por rango de fechas
    @GetMapping("/filtrar/fecha")
    public ResponseEntity<List<IngresoResponse>> getIngresosByFechaRange(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Long idCliente) {
        List<IngresoResponse> ingresos = ingresoService.findByFechaBetween(fechaInicio, fechaFin, idCliente);
        return ResponseEntity.ok(ingresos);
    }

    // Filtrar por rango de fechas
    @GetMapping("/filtrar/fecha")
    public ResponseEntity<List<IngresoResponse>> getIngresosByFechaRange(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Long idCliente) {
        List<IngresoResponse> ingresos = ingresoService.findByFechaBetween(fechaInicio, fechaFin, idCliente);
        return ResponseEntity.ok(ingresos);
    }

}
