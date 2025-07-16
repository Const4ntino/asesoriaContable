package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findAllByObligacion_Cliente_Id(Long idCliente);

    @Query("SELECT p FROM Pago p WHERE p.obligacion.cliente.contador.id = :contadorId")
    List<Pago> findAllByContadorId(@Param("contadorId") Long contadorId);
}
