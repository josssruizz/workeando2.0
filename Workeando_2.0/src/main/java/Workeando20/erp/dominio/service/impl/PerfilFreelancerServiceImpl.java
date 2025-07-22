package Workeando20.erp.dominio.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.model.PerfilFreelancer;
import Workeando20.erp.dominio.repository.PerfilFreelancerRepository;
import Workeando20.erp.dominio.service.PerfilFreelancerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilFreelancerServiceImpl implements PerfilFreelancerService {

    private final PerfilFreelancerRepository perfilFreelancerRepository;

    @Override
    public Optional<PerfilFreelancer> obtenerPerfilPorUsuarioId(Long usuarioId) {
        return perfilFreelancerRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional("dominioTransactionManager")
    public PerfilFreelancer actualizarPerfil(Long usuarioId, String telefono, String habilidades, String portafolio) {
        Optional<PerfilFreelancer> perfilOpt = perfilFreelancerRepository.findByUsuarioId(usuarioId);
        PerfilFreelancer perfil;

        if (perfilOpt.isPresent()) {
            perfil = perfilOpt.get();
        } else {
            perfil = new PerfilFreelancer();
            perfil.setUsuarioId(usuarioId);
        }

        // Eliminar experienciaLimpia
        // String experienciaLimpia = experiencia != null ? experiencia.trim() : null;

        String habilidadesLimpias = null;
        if (habilidades != null && !habilidades.isEmpty()) {
            String[] habilidadesArray = habilidades.split(",");
            java.util.Set<String> habilidadesSet = new java.util.LinkedHashSet<>();
            for (String h : habilidadesArray) {
                String habTrim = h.trim();
                if (!habTrim.isEmpty()) {
                    habilidadesSet.add(habTrim);
                }
            }
            habilidadesLimpias = String.join(", ", habilidadesSet);
        }

        String portafolioLimpio = portafolio != null ? portafolio.trim() : null;

        // Actualizar los datos del perfil
        perfil.setTelefono(telefono);
        // perfil.setExperiencia(experienciaLimpia); // Eliminar esta línea
        perfil.setHabilidades(habilidadesLimpias);
        perfil.setPortafolio(portafolioLimpio);

        // Guardar en la base de datos
        return perfilFreelancerRepository.save(perfil);
    }

    @Override
    @Transactional("dominioTransactionManager")
    public PerfilFreelancer actualizarPerfilConAcercaDeMi(Long usuarioId, String telefono, String experiencia, String habilidades, String portafolio, String acercaDeMi) {
        Optional<PerfilFreelancer> perfilOpt = perfilFreelancerRepository.findByUsuarioId(usuarioId);
        PerfilFreelancer perfil;

        if (perfilOpt.isPresent()) {
            perfil = perfilOpt.get();
        } else {
            perfil = new PerfilFreelancer();
            perfil.setUsuarioId(usuarioId);
        }

        String habilidadesLimpias = null;
        if (habilidades != null && !habilidades.isEmpty()) {
            String[] habilidadesArray = habilidades.split(",");
            java.util.Set<String> habilidadesSet = new java.util.LinkedHashSet<>();
            for (String h : habilidadesArray) {
                String habTrim = h.trim();
                if (!habTrim.isEmpty()) {
                    habilidadesSet.add(habTrim);
                }
            }
            habilidadesLimpias = String.join(", ", habilidadesSet);
        }

        String portafolioLimpio = portafolio != null ? portafolio.trim() : null;
        String acercaDeMiLimpio = acercaDeMi != null ? acercaDeMi.trim() : null;

        // Actualizar los datos del perfil
        perfil.setTelefono(telefono);
        perfil.setHabilidades(habilidadesLimpias);
        perfil.setPortafolio(portafolioLimpio);
        perfil.setAcercaDeMi(acercaDeMiLimpio);

        // Guardar en la base de datos
        return perfilFreelancerRepository.save(perfil);
    }
}
