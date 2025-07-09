package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.AlertaContadorRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaContadorResponse;
import com.asesoria.contable.app_ac.model.entity.AlertaContador;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlertaContadorMapper {

    @Mapping(source = "contador.id", target = "idContador")
    AlertaContadorResponse toAlertaContadorResponse(AlertaContador alertaContador);

    @Mapping(target = "contador", ignore = true)
    AlertaContador toAlertaContador(AlertaContadorRequest request);

    @Mapping(target = "contador", ignore = true)
    void updateAlertaContadorFromRequest(AlertaContadorRequest request, @MappingTarget AlertaContador alertaContador);
}
