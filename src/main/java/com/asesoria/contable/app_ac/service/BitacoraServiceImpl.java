package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.mapper.BitacoraMapper;
import com.asesoria.contable.app_ac.model.dto.BitacoraResponse;
import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.BitacoraRepository;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import com.asesoria.contable.app_ac.specification.BitacoraSpecification;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BitacoraServiceImpl implements BitacoraService {

    private final BitacoraRepository bitacoraRepository;
    private final BitacoraMapper bitacoraMapper;

    @Override
    public void registrarMovimiento(Usuario usuario, Modulo modulo, Accion accion, String descripcion) {
        if (usuario == null) {
            System.err.println("⚠ No se puede registrar bitácora: usuario no autenticado.");
            return; // o podrías lanzar una excepción custom si lo prefieres
        }

        BitacoraMovimiento movimiento = new BitacoraMovimiento();
        movimiento.setUsuario(usuario);
        movimiento.setRol(usuario.getRol());
        movimiento.setModulo(modulo);
        movimiento.setAccion(accion);
        movimiento.setDescripcion(descripcion);
        bitacoraRepository.save(movimiento);
    }

    @Override
    public Page<BitacoraResponse> findMyBitacora(
            Long usuarioId,
            Modulo modulo,
            Accion accion,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Pageable pageable) {

        return bitacoraRepository.findAll(
                BitacoraSpecification.filtrarBitacora(usuarioId, null, modulo, accion, fechaDesde, fechaHasta),
                pageable
        ).map(bitacoraMapper::toBitacoraResponse);
    }

    @Override
    public Page<BitacoraResponse> findAllBitacora(
            String searchTerm,
            Modulo modulo,
            Accion accion,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Pageable pageable) {

        return bitacoraRepository.findAll(
                BitacoraSpecification.filtrarBitacora(null, searchTerm, modulo, accion, fechaDesde, fechaHasta),
                pageable
        ).map(bitacoraMapper::toBitacoraResponse);
    }
}