package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse;
import com.asesoria.contable.app_ac.model.entity.Contador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContadorRepository extends JpaRepository<Contador, Long>, JpaSpecificationExecutor<Contador> {
    Optional<Contador> findByUsuarioId(Long usuarioId);

    @Query("SELECT new com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse(c.id, c.nombres, c.apellidos, COUNT(cl)) FROM Contador c LEFT JOIN Cliente cl ON c.id = cl.contador.id GROUP BY c.id ORDER BY COUNT(cl) DESC")
    List<ContadorClienteCountResponse> findTopContadoresByClientes(Pageable pageable);

    @Query("SELECT new com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse(c.id, c.nombres, c.apellidos, COUNT(cl)) FROM Contador c LEFT JOIN Cliente cl ON c.id = cl.contador.id GROUP BY c.id ORDER BY COUNT(cl) ASC")
    List<ContadorClienteCountResponse> findBottomContadoresByClientes(Pageable pageable);
}
