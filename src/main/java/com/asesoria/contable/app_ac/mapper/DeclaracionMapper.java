package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.DeclaracionRequest;
import com.asesoria.contable.app_ac.model.dto.DeclaracionResponse;
import com.asesoria.contable.app_ac.model.entity.Declaracion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeclaracionMapper {

    @Mapping(source = "cliente.id", target = "idCliente")
    DeclaracionResponse toDeclaracionResponse(Declaracion declaracion);

    @Mapping(target = "cliente", ignore = true)
    Declaracion toDeclaracion(DeclaracionRequest request);

    @Mapping(target = "cliente", ignore = true)
    void updateDeclaracionFromRequest(DeclaracionRequest request, @MappingTarget Declaracion declaracion);
}
