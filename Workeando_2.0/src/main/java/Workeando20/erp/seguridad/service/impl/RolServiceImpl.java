package Workeando20.erp.seguridad.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.repository.PermisoRepository;
import Workeando20.erp.seguridad.repository.RolRepository;
import Workeando20.erp.seguridad.service.RolService;

@Service
@Transactional("seguridadTransactionManager")
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    
    private static final List<String> ROLES_NO_ELIMINABLES = Arrays.asList("ADMIN");

    @Autowired
    public RolServiceImpl(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Optional<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Optional<Rol> obtenerRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Rol crearRol(String nombre) {
        if (rolRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + nombre);
        }
        
        Rol nuevoRol = Rol.builder()
                .nombre(nombre)
                .build();
        
        return rolRepository.save(nuevoRol);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public void eliminarRol(Long id) {
        Optional<Rol> rolOptional = rolRepository.findById(id);
        
        if (rolOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el rol con ID: " + id);
        }
        
        Rol rol = rolOptional.get();
        
        if (!esRolEliminable(rol.getNombre())) {
            throw new IllegalArgumentException("El rol " + rol.getNombre() + " no puede ser eliminado");
        }
        
        rolRepository.delete(rol);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Rol asignarPermisoARol(Long rolId, Long permisoId) {
        Optional<Rol> rolOptional = rolRepository.findById(rolId);
        Optional<Permiso> permisoOptional = permisoRepository.findById(permisoId);
        
        if (rolOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el rol con ID: " + rolId);
        }
        
        if (permisoOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId);
        }
        
        Rol rol = rolOptional.get();
        Permiso permiso = permisoOptional.get();
        
        rol.addPermiso(permiso);
        
        return rolRepository.save(rol);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_USUARIOS')")
    public Rol revocarPermisoDeRol(Long rolId, Long permisoId) {
        Optional<Rol> rolOptional = rolRepository.findById(rolId);
        Optional<Permiso> permisoOptional = permisoRepository.findById(permisoId);
        
        if (rolOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el rol con ID: " + rolId);
        }
        
        if (permisoOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el permiso con ID: " + permisoId);
        }
        
        Rol rol = rolOptional.get();
        Permiso permiso = permisoOptional.get();
        
        rol.removePermiso(permiso);
        
        return rolRepository.save(rol);
    }

    @Override
    public boolean esRolEliminable(String nombreRol) {
        return !ROLES_NO_ELIMINABLES.contains(nombreRol.toUpperCase());
    }
}
