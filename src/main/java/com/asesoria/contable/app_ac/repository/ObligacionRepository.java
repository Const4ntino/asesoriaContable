package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Obligacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ObligacionRepository extends JpaRepository<Obligacion, Long> {
    List<Obligacion> findByClienteId(Long clienteId);

    @Query("SELECT o FROM Obligacion o WHERE o.periodo = (SELECT MAX(o2.periodo) FROM Obligacion o2 WHERE o2.cliente.id = o.cliente.id AND o2.cliente.id IN :clienteIds) AND o.cliente.id IN :clienteIds")
    List<Obligacion> findLatestObligacionesForClients(@Param("clienteIds") List<Long> clienteIds);
}
