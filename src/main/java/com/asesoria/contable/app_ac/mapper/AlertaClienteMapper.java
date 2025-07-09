package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.AlertaClienteRequest;
import com.asesoria.contable.app_ac.model.dto.AlertaClienteResponse;
import com.asesoria.contable.app_ac.model.entity.AlertaCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlertaClienteMapper {

    @Mapping(source = "cliente.id", target = "idCliente")
    AlertaClienteResponse toAlertaClienteResponse(AlertaCliente alertaCliente);

    @Mapping(target = "cliente", ignore = true)
    AlertaCliente toAlertaCliente(AlertaClienteRequest request);

    @Mapping(target = "cliente", ignore = true)
    void updateAlertaClienteFromRequest(AlertaClienteRequest request, @MappingTarget AlertaCliente alertaCliente);
}
