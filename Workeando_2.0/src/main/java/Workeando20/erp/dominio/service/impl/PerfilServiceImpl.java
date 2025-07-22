package Workeando20.erp.dominio.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.model.PerfilEmpleador;
import Workeando20.erp.dominio.model.PerfilFreelancer;
import Workeando20.erp.dominio.model.TipoRolRegistro;
import Workeando20.erp.dominio.repository.PerfilEmpleadorRepository;
import Workeando20.erp.dominio.repository.PerfilFreelancerRepository;
import Workeando20.erp.dominio.service.PerfilService;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final PerfilFreelancerRepository freelancerRepo;
    private final PerfilEmpleadorRepository empleadorRepo;
    private final UsuarioRepository usuarioRepository; // Necesario para referenciar usuario

    @Override
    @Transactional("dominioTransactionManager")
    public void crearPerfilInicial(TipoRolRegistro rol, Long usuarioId) {
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId); // proxy

        if (rol == TipoRolRegistro.FREELANCER) {
            PerfilFreelancer pf = PerfilFreelancer.builder()
                    .usuarioId(usuario.getId())
                    .telefono(null)
                    // .experiencia(null) // Eliminar esta línea
                    .habilidades(null)
                    .portafolio(null)
                    .acercaDeMi(null) // Asegurarse de inicializar acercaDeMi
                    .build();
            freelancerRepo.save(pf);

        } else if (rol == TipoRolRegistro.EMPLEADOR) {
            PerfilEmpleador pe = PerfilEmpleador.builder()
                    .usuarioId(usuario.getId())
                    .nombrePublico(usuario.getNombre())
                    .tipoEmpleador("PERSONA")
                    .descripcion(null)
                    .build();
            empleadorRepo.save(pe);
        }
    }

    @Transactional("dominioTransactionManager")
    public PerfilEmpleador obtenerPerfilPorUsuarioId(Long usuarioId) {
        return empleadorRepo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil empleador no encontrado para usuarioId: " + usuarioId));
    }

    @Transactional("dominioTransactionManager")
    public PerfilEmpleador actualizarPerfil(Long usuarioId, PerfilEmpleador perfilActualizado) {
        PerfilEmpleador perfilExistente = empleadorRepo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil empleador no encontrado para usuarioId: " + usuarioId));

        perfilExistente.setTipoEmpleador(perfilActualizado.getTipoEmpleador());
        perfilExistente.setNombrePublico(perfilActualizado.getNombrePublico());
        perfilExistente.setDescripcion(perfilActualizado.getDescripcion());
        perfilExistente.setAcercaDeNosotros(perfilActualizado.getAcercaDeNosotros());

        return empleadorRepo.save(perfilExistente);
    }
}
