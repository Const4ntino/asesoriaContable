package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse;
import com.asesoria.contable.app_ac.model.entity.Contador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContadorRepository extends JpaRepository<Contador, Long>, JpaSpecificationExecutor<Contador> {
    Optional<Contador> findByUsuarioId(Long usuarioId);

    @Query("SELECT new com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse(c.id, c.nombres, c.apellidos, COUNT(cl)) FROM Contador c LEFT JOIN Cliente cl ON c.id = cl.contador.id GROUP BY c.id ORDER BY COUNT(cl) DESC")
    List<ContadorClienteCountResponse> findTopContadoresByClientes(Pageable pageable);

    @Query("SELECT new com.asesoria.contable.app_ac.model.dto.ContadorClienteCountResponse(c.id, c.nombres, c.apellidos, COUNT(cl)) FROM Contador c LEFT JOIN Cliente cl ON c.id = cl.contador.id GROUP BY c.id ORDER BY COUNT(cl) ASC")
    List<ContadorClienteCountResponse> findBottomContadoresByClientes(Pageable pageable);
    
    @Query("SELECT c FROM Contador c JOIN Cliente cl ON c.id = cl.contador.id " +
           "WHERE (:search IS NULL OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR c.dni LIKE CONCAT('%', :search, '%')) " +
           "GROUP BY c.id")
    Page<Contador> findContadoresWithClientes(@Param("search") String search, Pageable pageable);
}
