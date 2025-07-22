package Workeando20.erp.seguridad.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        Set<GrantedAuthority> authorities = new HashSet<>();

        try {
            // Añadir permisos de roles
            for (Rol rol : usuario.getRoles()) {
                // Añade el rol con el prefijo "ROLE_"
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));

                // Añade todos los permisos asociados a ese rol
                for (Permiso permiso : rol.getPermisos()) {
                    authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
                }
            }

            // Añadir permisos directos del usuario
            if (usuario.getPermisosDirectos() != null) {
                for (Permiso permiso : usuario.getPermisosDirectos()) {
                    authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
                }
            }

            System.out.println("Usuario: " + correo);
            System.out.println("Permisos de roles: ");
            usuario.getRoles().forEach(rol -> {
                System.out.println("- Rol: " + rol.getNombre());
                rol.getPermisos().forEach(permiso -> System.out.println("  - " + permiso.getNombre()));
            });
            System.out.println("Permisos directos: ");
            if (usuario.getPermisosDirectos() != null) {
                usuario.getPermisosDirectos().forEach(permiso -> System.out.println("- " + permiso.getNombre()));
            } else {
                System.out.println("- No hay permisos directos");
            }
            System.out.println("Autoridades finales: ");
            authorities.forEach(a -> System.out.println("- " + a.getAuthority()));

        } catch (Exception e) {
            System.err.println("Error al cargar permisos para usuario " + correo + ": " + e.getMessage());
            e.printStackTrace();
            
            // En caso de error, solo cargar permisos de roles
            for (Rol rol : usuario.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
                for (Permiso permiso : rol.getPermisos()) {
                    authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
                }
            }
        }

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.getActivo(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities);
    }
}
