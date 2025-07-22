package com.asesoria.contable.app_ac.controller;

import com.asesoria.contable.app_ac.model.dto.BitacoraResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.service.BitacoraService;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/bitacora")
@RequiredArgsConstructor
public class BitacoraController {

    private final BitacoraService bitacoraService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE', 'CONTADOR')")
    @GetMapping("/mis-movimientos")
    public ResponseEntity<Page<BitacoraResponse>> getMyBitacora(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) Modulo modulo,
            @RequestParam(required = false) Accion accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<BitacoraResponse> bitacoraPage = bitacoraService.findMyBitacora(
                usuario.getId(),
                modulo,
                accion,
                fechaDesde,
                fechaHasta,
                pageable);

        return ResponseEntity.ok(bitacoraPage);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<BitacoraResponse>> getAllBitacora(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Modulo modulo,
            @RequestParam(required = false) Accion accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaMovimiento") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<BitacoraResponse> bitacoraPage = bitacoraService.findAllBitacora(
                searchTerm,
                modulo,
                accion,
                fechaDesde,
                fechaHasta,
                pageable);

        return ResponseEntity.ok(bitacoraPage);
    }
}