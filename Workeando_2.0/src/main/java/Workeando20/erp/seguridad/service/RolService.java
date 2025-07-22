package Workeando20.erp.seguridad.service;

import java.util.List;
import java.util.Optional;

import Workeando20.erp.seguridad.model.Rol;

public interface RolService {
    
    List<Rol> obtenerTodosLosRoles();
    
    Optional<Rol> obtenerRolPorId(Long id);
    
    Optional<Rol> obtenerRolPorNombre(String nombre);
    
    Rol crearRol(String nombre);
    
    void eliminarRol(Long id);
    
    Rol asignarPermisoARol(Long rolId, Long permisoId);
    
    Rol revocarPermisoDeRol(Long rolId, Long permisoId);
    
    boolean esRolEliminable(String nombreRol);
}
