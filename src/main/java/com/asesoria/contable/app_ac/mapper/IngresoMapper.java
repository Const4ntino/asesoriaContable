package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.IngresoRequest;
import com.asesoria.contable.app_ac.model.dto.IngresoResponse;
import com.asesoria.contable.app_ac.model.entity.Ingreso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class})
public interface IngresoMapper {

    @Mapping(target = "cliente", ignore = true)
    Ingreso toIngreso(IngresoRequest request);

    @Mapping(target = "cliente", source = "cliente")
    IngresoResponse toIngresoResponse(Ingreso ingreso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateIngresoFromRequest(IngresoRequest request, @MappingTarget Ingreso ingreso);
}
