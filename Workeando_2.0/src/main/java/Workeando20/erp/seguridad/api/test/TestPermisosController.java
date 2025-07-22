package Workeando20.erp.seguridad.api.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/test")
public class TestPermisosController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-permisos")
    public ResponseEntity<Map<String, Object>> obtenerMisPermisos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null && auth.isAuthenticated()) {
            String correo = auth.getName();
            
            // Obtener permisos del contexto de seguridad
            Set<String> permisosContexto = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
            
            // Obtener usuario de la base de datos
            Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
            
            response.put("usuario", correo);
            response.put("permisosEnContexto", permisosContexto);
            
            if (usuario != null) {
                Set<String> permisosDirectos = usuario.getPermisosDirectos().stream()
                    .map(p -> p.getNombre())
                    .collect(Collectors.toSet());
                
                Set<String> permisosDeRoles = usuario.getRoles().stream()
                    .flatMap(rol -> rol.getPermisos().stream())
                    .map(p -> p.getNombre())
                    .collect(Collectors.toSet());
                
                response.put("permisosDirectosEnBD", permisosDirectos);
                response.put("permisosDeRolesEnBD", permisosDeRoles);
                response.put("roles", usuario.getRoles().stream()
                    .map(r -> r.getNombre())
                    .collect(Collectors.toSet()));
            }
            
            return ResponseEntity.ok(response);
        }
        
        response.put("error", "Usuario no autenticado");
        return ResponseEntity.badRequest().body(response);
    }
}
