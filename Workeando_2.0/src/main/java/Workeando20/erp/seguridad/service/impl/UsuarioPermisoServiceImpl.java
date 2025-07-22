package Workeando20.erp.seguridad.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.seguridad.dto.PermisoUsuarioDTO;
import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.PermisoRepository;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import Workeando20.erp.seguridad.service.UsuarioPermisoService;

@Service
@Transactional("seguridadTransactionManager")
public class UsuarioPermisoServiceImpl implements UsuarioPermisoService {

    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;

    @Autowired
    public UsuarioPermisoServiceImpl(UsuarioRepository usuarioRepository, PermisoRepository permisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.permisoRepository = permisoRepository;
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Usuario asignarPermisoAUsuario(Long usuarioId, Long permisoId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        Optional<Permiso> permisoOptional = permisoRepository.findById(permisoId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + usuarioId);
        }

        if (permisoOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId);
        }

        Usuario usuario = usuarioOptional.get();
        Permiso permiso = permisoOptional.get();

        // Agregar solo del lado del usuario para evitar problemas de lazy loading
        usuario.getPermisosDirectos().add(permiso);

        usuarioRepository.save(usuario);

        // Crear un usuario simple para devolver sin relaciones problemáticas
        Usuario usuarioSimple = new Usuario();
        usuarioSimple.setId(usuario.getId());
        usuarioSimple.setNombre(usuario.getNombre());
        usuarioSimple.setCorreo(usuario.getCorreo());
        usuarioSimple.setActivo(usuario.getActivo());

        return usuarioSimple;
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Usuario revocarPermisoDeUsuario(Long usuarioId, Long permisoId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        Optional<Permiso> permisoOptional = permisoRepository.findById(permisoId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + usuarioId);
        }

        if (permisoOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId);
        }

        Usuario usuario = usuarioOptional.get();
        Permiso permiso = permisoOptional.get();

        // Remover solo del lado del usuario para evitar problemas de lazy loading
        usuario.getPermisosDirectos().remove(permiso);

        usuarioRepository.save(usuario);

        // Crear un usuario simple para devolver sin relaciones problemáticas
        Usuario usuarioSimple = new Usuario();
        usuarioSimple.setId(usuario.getId());
        usuarioSimple.setNombre(usuario.getNombre());
        usuarioSimple.setCorreo(usuario.getCorreo());
        usuarioSimple.setActivo(usuario.getActivo());

        return usuarioSimple;
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Set<Permiso> obtenerPermisosDirectosDeUsuario(Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + usuarioId);
        }

        return usuarioOptional.get().getPermisosDirectos();
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Set<Permiso> obtenerTodosLosPermisosDeUsuario(Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el usuario con ID: " + usuarioId);
        }

        Usuario usuario = usuarioOptional.get();
        Set<Permiso> todosLosPermisos = new HashSet<>();

        // Añadir permisos directos
        todosLosPermisos.addAll(usuario.getPermisosDirectos());

        // Añadir permisos de roles
        for (Rol rol : usuario.getRoles()) {
            todosLosPermisos.addAll(rol.getPermisos());
        }

        return todosLosPermisos;
    }

    @Override
    public List<Permiso> obtenerTodosLosPermisos() {
        try {
            return permisoRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener permisos: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<PermisoUsuarioDTO> obtenerPermisosConHeredado(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Set<Permiso> directos = usuario.getPermisosDirectos();
        Set<Permiso> porRol = usuario.getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .collect(Collectors.toSet());

        Set<Permiso> totales = new HashSet<>();
        totales.addAll(directos);
        totales.addAll(porRol);

        return totales.stream()
                .map(p -> new PermisoUsuarioDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        !directos.contains(p)
                ))
                .collect(Collectors.toList());
    }

}
