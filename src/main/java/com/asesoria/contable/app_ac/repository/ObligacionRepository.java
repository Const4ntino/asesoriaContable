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

    @Query(value = """
                SELECT * FROM obligacion o 
                WHERE (o.id_cliente, o.periodo) IN (
                    SELECT id_cliente, MAX(periodo)
                    FROM obligacion
                    WHERE id_cliente IN (:clienteIds)
                    GROUP BY id_cliente
                )
            """, nativeQuery = true)
    List<Obligacion> findLatestObligacionesForClients(@Param("clienteIds") List<Long> clienteIds);
}
