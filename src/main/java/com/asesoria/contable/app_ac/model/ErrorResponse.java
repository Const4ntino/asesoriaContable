package com.asesoria.contable.app_ac.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ErrorResponse {

    private String codigo;
    private HttpStatus status;
    private String mensaje;
    private List<String> detalleMensaje;
    private LocalDateTime marcaDeTiempo;
}