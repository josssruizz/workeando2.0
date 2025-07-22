package Workeando20.erp.dominio.repository;

import Workeando20.erp.dominio.model.PerfilFreelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilFreelancerRepository extends JpaRepository<PerfilFreelancer, Long> {

    Optional<PerfilFreelancer> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
