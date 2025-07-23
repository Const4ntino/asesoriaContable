package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Declaracion;
import com.asesoria.contable.app_ac.utils.enums.DeclaracionEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeclaracionRepository extends JpaRepository<Declaracion, Long>, JpaSpecificationExecutor<Declaracion>, DeclaracionRepositoryCustom {
    List<Declaracion> findByClienteId(Long clienteId);

    Optional<Declaracion> findByClienteIdAndPeriodoTributario(Long clienteId, LocalDate periodoTributario);
    Optional<Declaracion> findFirstByClienteIdAndEstadoOrderByPeriodoTributarioAsc(Long clienteId, DeclaracionEstado estado);

    @Query("SELECT d FROM Declaracion d WHERE d.periodoTributario = (SELECT MAX(d2.periodoTributario) FROM Declaracion d2 WHERE d2.cliente.id = d.cliente.id AND d2.cliente.id IN :clienteIds) AND d.cliente.id IN :clienteIds")
    List<Declaracion> findLatestDeclarationsForClients(@Param("clienteIds") List<Long> clienteIds);
}

