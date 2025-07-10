package com.asesoria.contable.app_ac.mapper;

import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;
import com.asesoria.contable.app_ac.model.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(source = "obligacion.id", target = "idObligacion")
    PagoResponse toPagoResponse(Pago pago);

    @Mapping(target = "obligacion", ignore = true)
    Pago toPago(PagoRequest request);

    @Mapping(target = "obligacion", ignore = true)
    void updatePagoFromRequest(PagoRequest request, @MappingTarget Pago pago);
}
