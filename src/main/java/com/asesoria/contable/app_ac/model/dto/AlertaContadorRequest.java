package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertaContadorRequest {

    @NotNull(message = "El ID del contador no puede ser nulo")
    private Long idContador;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    private EstadoAlerta estado;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    @NotNull(message = "La fecha de expiración no puede ser nula")
    private LocalDateTime fechaExpiracion;
}
