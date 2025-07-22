package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.BitacoraResponse;
import com.asesoria.contable.app_ac.model.entity.BitacoraMovimiento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface BitacoraMapper {
    BitacoraResponse toBitacoraResponse(BitacoraMovimiento bitacoraMovimiento);
}