package Workeando20.erp.seguridad.api.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.service.RolService;
import Workeando20.erp.seguridad.service.UsuarioPermisoService;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRolesController {

    private final RolService rolService;
    private final UsuarioPermisoService usuarioPermisoService;

    @Autowired
    public AdminRolesController(RolService rolService, UsuarioPermisoService usuarioPermisoService) {
        this.rolService = rolService;
        this.usuarioPermisoService = usuarioPermisoService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> obtenerTodosLosRoles() {
        try {
            List<Rol> roles = rolService.obtenerTodosLosRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crearRol(@RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre del rol es requerido");
            }
            
            Rol nuevoRol = rolService.crearRol(nombre.trim());
            return ResponseEntity.ok(nuevoRol);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        try {
            rolService.eliminarRol(id);
            return ResponseEntity.ok().body("Rol eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @PostMapping("/{id}/permisos")
    public ResponseEntity<?> asignarPermisoARol(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        try {
            Long permisoId = request.get("permisoId");
            if (permisoId == null) {
                return ResponseEntity.badRequest().body("El ID del permiso es requerido");
            }
            
            Rol rolActualizado = rolService.asignarPermisoARol(id, permisoId);
            return ResponseEntity.ok(rolActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @DeleteMapping("/{id}/permisos/{permisoId}")
    public ResponseEntity<?> revocarPermisoDeRol(@PathVariable Long id, @PathVariable Long permisoId) {
        try {
            Rol rolActualizado = rolService.revocarPermisoDeRol(id, permisoId);
            return ResponseEntity.ok(rolActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @GetMapping("/permisos")
    public ResponseEntity<List<Permiso>> obtenerTodosLosPermisos() {
        try {
            List<Permiso> permisos = usuarioPermisoService.obtenerTodosLosPermisos();
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
