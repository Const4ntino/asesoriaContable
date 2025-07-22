package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import com.asesoria.contable.app_ac.utils.enums.Rol;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BitacoraResponse {
    private Long id;
    private UsuarioResponse usuario;
    private Rol rol;
    private Modulo modulo;
    private Accion accion;
    private String descripcion;
    private LocalDateTime fechaMovimiento;
}