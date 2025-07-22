package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;

public interface BitacoraService {
    void registrarMovimiento(Usuario usuario, Modulo modulo, Accion accion, String descripcion);
}
