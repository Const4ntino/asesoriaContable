package com.asesoria.contable.app_ac.repository;

import com.asesoria.contable.app_ac.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u FROM Usuario u " +
            "WHERE u.rol = 'CLIENTE' " +
            "AND u.id NOT IN (SELECT c.usuario.id FROM Cliente c WHERE c.usuario IS NOT NULL) " +
            "AND u.id NOT IN (SELECT co.usuario.id FROM Contador co WHERE co.usuario IS NOT NULL) " +
            "AND u.username LIKE %:username%")
    List<Usuario> findUsuariosClientesLibres(@Param("username") String username);

    @Query("SELECT u FROM Usuario u " +
            "WHERE u.rol = 'CONTADOR' " +
            "AND u.id NOT IN (SELECT co.usuario.id FROM Contador co WHERE co.usuario IS NOT NULL) " +
            "AND u.id NOT IN (SELECT c.usuario.id FROM Cliente c WHERE c.usuario IS NOT NULL) " +
            "AND u.username LIKE %:username%")
    List<Usuario> findUsuariosContadoresLibres(@Param("username") String username);
}
