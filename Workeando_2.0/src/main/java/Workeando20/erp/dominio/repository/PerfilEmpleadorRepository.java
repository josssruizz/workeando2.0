package Workeando20.erp.dominio.repository;

import Workeando20.erp.dominio.model.PerfilEmpleador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilEmpleadorRepository extends JpaRepository<PerfilEmpleador, Long> {

    Optional<PerfilEmpleador> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
