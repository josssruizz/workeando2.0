package Workeando20.erp.seguridad.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import Workeando20.erp.seguridad.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = {"roles", "roles.permisos", "permisosDirectos"})
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
