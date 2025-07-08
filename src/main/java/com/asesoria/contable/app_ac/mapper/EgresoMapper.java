package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.EgresoRequest;
import com.asesoria.contable.app_ac.model.dto.EgresoResponse;
import com.asesoria.contable.app_ac.model.entity.Egreso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ClienteMapper.class})
public interface EgresoMapper {

    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "montoIgv", ignore = true)
    Egreso toEgreso(EgresoRequest request);

    @Mapping(target = "cliente", source = "cliente")
    @Mapping(target = "montoIgv", source = "montoIgv")
    EgresoResponse toEgresoResponse(Egreso egreso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "montoIgv", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void updateEgresoFromRequest(EgresoRequest request, @MappingTarget Egreso egreso);
}
