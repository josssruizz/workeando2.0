package Workeando20.erp.config;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.PermisoRepository;
import Workeando20.erp.seguridad.repository.RolRepository;
import Workeando20.erp.seguridad.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PermisoRepository permisoRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional("seguridadTransactionManager")
    public void run(String... args) throws Exception {
        
        // Crear permisos si no existen
        List<String> nombresPermisos = Arrays.asList(
            "GESTIONAR_USUARIOS",
            "GESTIONAR_CATEGORIAS", 
            "VER_PANEL_ADMIN",
            "VER_PANEL_FREELANCER",
            "VER_PANEL_EMPLEADOR",
            "CREAR_PROYECTOS",
            "EDITAR_PROYECTOS",
            "ELIMINAR_PROYECTOS",
            "VER_PROYECTOS",
            "POSTULAR_PROYECTOS",
            "GESTIONAR_POSTULACIONES",
            "CREAR_CONTRATOS",
            "GESTIONAR_CONTRATOS",
            "ENVIAR_MENSAJES",
            "RECIBIR_MENSAJES",
            "CALIFICAR_USUARIOS",
            "VER_CALIFICACIONES"
        );

        Set<Permiso> todosLosPermisos = nombresPermisos.stream()
            .map(nombre -> {
                return permisoRepository.findByNombre(nombre)
                    .orElseGet(() -> {
                        Permiso nuevoPermiso = Permiso.builder()
                            .nombre(nombre)
                            .build();
                        return permisoRepository.save(nuevoPermiso);
                    });
            })
            .collect(Collectors.toSet());

        // Crear roles si no existen
        
        // ROL ADMIN - Tiene todos los permisos
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
            .orElseGet(() -> {
                Rol nuevoRol = Rol.builder()
                    .nombre("ADMIN")
                    .build();
                return rolRepository.save(nuevoRol);
            });
        
        // Asignar todos los permisos al ADMIN
        for (Permiso permiso : todosLosPermisos) {
            if (!rolAdmin.getPermisos().contains(permiso)) {
                rolAdmin.addPermiso(permiso);
            }
        }
        rolRepository.save(rolAdmin);

        // ROL FREELANCER - Permisos específicos para freelancers
        Rol rolFreelancer = rolRepository.findByNombre("FREELANCER")
            .orElseGet(() -> {
                Rol nuevoRol = Rol.builder()
                    .nombre("FREELANCER")
                    .build();
                return rolRepository.save(nuevoRol);
            });
        
        // Permisos para FREELANCER
        List<String> permisosFreelancer = Arrays.asList(
            "VER_PANEL_FREELANCER",
            "VER_PROYECTOS",
            "POSTULAR_PROYECTOS",
            "GESTIONAR_CONTRATOS",
            "ENVIAR_MENSAJES",
            "RECIBIR_MENSAJES",
            "CALIFICAR_USUARIOS",
            "VER_CALIFICACIONES"
        );
        
        for (String nombrePermiso : permisosFreelancer) {
            Permiso permiso = todosLosPermisos.stream()
                .filter(p -> p.getNombre().equals(nombrePermiso))
                .findFirst()
                .orElse(null);
            if (permiso != null && !rolFreelancer.getPermisos().contains(permiso)) {
                rolFreelancer.addPermiso(permiso);
            }
        }
        rolRepository.save(rolFreelancer);

        // ROL EMPLEADOR - Permisos específicos para empleadores
        Rol rolEmpleador = rolRepository.findByNombre("EMPLEADOR")
            .orElseGet(() -> {
                Rol nuevoRol = Rol.builder()
                    .nombre("EMPLEADOR")
                    .build();
                return rolRepository.save(nuevoRol);
            });
        
        // Permisos para EMPLEADOR
        List<String> permisosEmpleador = Arrays.asList(
            "VER_PANEL_EMPLEADOR",
            "CREAR_PROYECTOS",
            "EDITAR_PROYECTOS",
            "ELIMINAR_PROYECTOS",
            "VER_PROYECTOS",
            "GESTIONAR_POSTULACIONES",
            "CREAR_CONTRATOS",
            "GESTIONAR_CONTRATOS",
            "ENVIAR_MENSAJES",
            "RECIBIR_MENSAJES",
            "CALIFICAR_USUARIOS",
            "VER_CALIFICACIONES"
        );
        
        for (String nombrePermiso : permisosEmpleador) {
            Permiso permiso = todosLosPermisos.stream()
                .filter(p -> p.getNombre().equals(nombrePermiso))
                .findFirst()
                .orElse(null);
            if (permiso != null && !rolEmpleador.getPermisos().contains(permiso)) {
                rolEmpleador.addPermiso(permiso);
            }
        }
        rolRepository.save(rolEmpleador);

        // Crear usuario administrador por defecto si no existe
        if (!usuarioRepository.existsByCorreo("admin@workeando.com")) {
            Usuario usuarioAdmin = Usuario.builder()
                .nombre("Administrador")
                .correo("admin@workeando.com")
                .contrasena(passwordEncoder.encode("admin123"))
                .activo(true)
                .build();
            
            usuarioAdmin.addRol(rolAdmin);
            usuarioRepository.save(usuarioAdmin);
            
            System.out.println("Usuario administrador creado:");
            System.out.println("Email: admin@workeando.com");
            System.out.println("Contraseña: admin123");
        }

        System.out.println("DataLoader ejecutado: Roles, permisos y usuario admin inicializados correctamente");
    }
}
