package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.BitacoraResponse;
import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BitacoraService {
    void registrarMovimiento(Usuario usuario, Modulo modulo, Accion accion, String descripcion);

    Page<BitacoraResponse> findMyBitacora(
            Long usuarioId,
            Modulo modulo,
            Accion accion,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Pageable pageable);

    Page<BitacoraResponse> findAllBitacora(
            String searchTerm,
            Modulo modulo,
            Accion accion,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Pageable pageable);
}