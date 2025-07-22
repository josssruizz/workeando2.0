package Workeando20.erp.dominio.service;

import java.util.Optional;

import Workeando20.erp.dominio.model.PerfilFreelancer;

public interface PerfilFreelancerService {

    Optional<PerfilFreelancer> obtenerPerfilPorUsuarioId(Long usuarioId);

    PerfilFreelancer actualizarPerfil(Long usuarioId, String telefono, String habilidades, String portafolio);

    PerfilFreelancer actualizarPerfilConAcercaDeMi(Long usuarioId, String telefono, String experiencia, String habilidades, String portafolio, String acercaDeMi);
}
