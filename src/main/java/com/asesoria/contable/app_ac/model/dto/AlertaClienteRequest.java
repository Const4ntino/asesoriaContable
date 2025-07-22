package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertaClienteRequest {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long idCliente;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    private EstadoAlerta estado;

    @NotNull(message = "La fecha de expiración no puede ser nula")
    private LocalDateTime fechaExpiracion;
}
