package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.ContadorResponse;
import com.asesoria.contable.app_ac.model.entity.Contador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface ContadorMapper {
    ContadorResponse toContadorResponse(Contador contador);
}
