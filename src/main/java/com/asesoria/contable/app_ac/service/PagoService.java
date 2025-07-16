package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.PagoClienteRequest;
import com.asesoria.contable.app_ac.model.dto.PagoContadorRequest;
import com.asesoria.contable.app_ac.model.dto.PagoRequest;
import com.asesoria.contable.app_ac.model.dto.PagoResponse;
import com.asesoria.contable.app_ac.model.entity.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoService {

    PagoResponse findById(Long id);
    List<PagoResponse> findAll();
    PagoResponse save(PagoRequest request);

    PagoResponse registrarPagoCliente(Long idObligacion, PagoClienteRequest pagoRequest);
    void rechazarPagoCliente(Long idPago, String comentario);

    PagoResponse registrarPagoContador(Long idObligacion, PagoContadorRequest pagoRequest);

    PagoResponse update(Long id, PagoRequest request);
    void deleteById(Long id);

    List<PagoResponse> buscarMisPagos(Usuario usuario, LocalDate periodoTributario, BigDecimal monto, String orden);

    PagoResponse actualizarEstado(Long idPago, String nuevoEstado);

    List<PagoResponse> buscarUltimosPagosDeMisClientes(
            Usuario usuario,
            Integer mes,
            String ordenMonto,
            String medioPago
    );
}
