package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.dto.RegimenClienteCountResponse;
import com.asesoria.contable.app_ac.model.entity.Cliente;
import com.asesoria.contable.app_ac.model.entity.Contador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    List<Cliente> findAllByContador(Contador contador);
    long countByContadorIsNotNull();
    long countByContadorIsNull();

    @Query("SELECT new com.asesoria.contable.app_ac.model.dto.RegimenClienteCountResponse(c.regimen, COUNT(c)) FROM Cliente c GROUP BY c.regimen")
    List<RegimenClienteCountResponse> countClientesByRegimen();

    @Query("SELECT c.contador.id, COUNT(c.id) FROM Cliente c WHERE c.contador.id IN :contadorIds GROUP BY c.contador.id")
    List<Object[]> countClientesByContadorIds(List<Long> contadorIds);
}
