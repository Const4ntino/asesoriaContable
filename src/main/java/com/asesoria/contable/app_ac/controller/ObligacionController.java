package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.*;
import com.asesoria.contable.app_ac.service.ObligacionService;
import com.asesoria.contable.app_ac.service.PagoService;
import com.asesoria.contable.app_ac.utils.enums.EstadoPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/obligaciones")
@RequiredArgsConstructor
public class ObligacionController {

    private final ObligacionService obligacionService;
    private final PagoService pagoService;

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
    @GetMapping("/mis-clientes/obligaciones")
    public ResponseEntity<List<ObligacionResponse>> filtrarObligaciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nombreCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "DESC") String orden
    ) {
        List<ObligacionResponse> lista = obligacionService.buscarObligaciones(usuario, estado, nombreCliente, desde, hasta, orden);
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mis-obligaciones")
    public ResponseEntity<List<ObligacionResponse>> getMisObligaciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) java.math.BigDecimal montoMaximo,
            @RequestParam(defaultValue = "DESC") String ordenFechaLimite
    ) {
        List<ObligacionResponse> obligaciones = obligacionService.buscarMisObligaciones(usuario, mes, anio, montoMaximo, ordenFechaLimite);
        return ResponseEntity.ok(obligaciones);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{idObligacion}/pagos/cliente")
    public ResponseEntity<PagoResponse> registrarPagoCliente(@PathVariable Long idObligacion, @Valid @RequestBody PagoClienteRequest pagoRequest) {
        PagoResponse pagoResponse = pagoService.registrarPagoCliente(idObligacion, pagoRequest);
        return new ResponseEntity<>(pagoResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CONTADOR')")
    @PostMapping("/{idObligacion}/pagos/contador")
    public ResponseEntity<PagoResponse> registrarPagoContador(@PathVariable Long idObligacion, @Valid @RequestBody PagoContadorRequest pagoRequest) {
        PagoResponse pagoResponse = pagoService.registrarPagoContador(idObligacion, pagoRequest);
        return new ResponseEntity<>(pagoResponse, HttpStatus.CREATED);
    }


}
