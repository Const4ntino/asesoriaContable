package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.ClienteResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface ClienteMapper {
    ClienteResponse toClienteResponse(Cliente cliente);
}
