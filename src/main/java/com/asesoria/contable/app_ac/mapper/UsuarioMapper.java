package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.UsuarioResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponse toUsuarioResponse(Usuario usuario);
}
