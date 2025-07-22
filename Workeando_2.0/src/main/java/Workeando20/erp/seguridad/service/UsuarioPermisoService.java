package Workeando20.erp.seguridad.service;

import java.util.List;
import java.util.Set;

import Workeando20.erp.seguridad.dto.PermisoUsuarioDTO;
import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Usuario;

public interface UsuarioPermisoService {
    
    Usuario asignarPermisoAUsuario(Long usuarioId, Long permisoId);
    
    Usuario revocarPermisoDeUsuario(Long usuarioId, Long permisoId);
    
    Set<Permiso> obtenerPermisosDirectosDeUsuario(Long usuarioId);
    
    Set<Permiso> obtenerTodosLosPermisosDeUsuario(Long usuarioId);
    
    List<Permiso> obtenerTodosLosPermisos();
    List<PermisoUsuarioDTO> obtenerPermisosConHeredado(Long usuarioId);
}
