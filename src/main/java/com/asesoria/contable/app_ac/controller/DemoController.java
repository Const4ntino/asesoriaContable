package com.asesoria.contable.app_ac.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity
@RequestMapping("/api/inicio")
public class DemoController {

    //    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/administrador")
    public String inicio() {
        return "Hola bienvenido al inicio Administrador.";
    }
}
