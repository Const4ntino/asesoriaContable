package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import com.asesoria.contable.app_ac.repository.BitacoraRepository;
import com.asesoria.contable.app_ac.utils.enums.Accion;
import com.asesoria.contable.app_ac.utils.enums.Modulo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BitacoraServiceImpl implements BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    @Override
    public void registrarMovimiento(Usuario usuario, Modulo modulo, Accion accion, String descripcion) {
        BitacoraMovimiento movimiento = new BitacoraMovimiento();
        movimiento.setUsuario(usuario);
        movimiento.setRol(usuario.getRol());
        movimiento.setModulo(modulo);
        movimiento.setAccion(accion);
        movimiento.setDescripcion(descripcion);
        bitacoraRepository.save(movimiento);
    }
}
