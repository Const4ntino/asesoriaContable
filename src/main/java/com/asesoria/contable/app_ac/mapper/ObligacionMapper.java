package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.ObligacionRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionResponse;
import com.asesoria.contable.app_ac.model.entity.Obligacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DeclaracionMapper.class, ClienteMapper.class})
public interface ObligacionMapper {

    @Mapping(source = "idCliente", target = "cliente.id")
    @Mapping(source = "idDeclaracion", target = "declaracion.id")
    Obligacion convertToEntity(ObligacionRequest obligacionRequest);

    ObligacionResponse convertToResponse(Obligacion obligacion);

    void updateEntity(ObligacionRequest obligacionRequest, @MappingTarget Obligacion obligacion);
}
