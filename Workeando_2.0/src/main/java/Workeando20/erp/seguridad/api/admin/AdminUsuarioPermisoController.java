package Workeando20.erp.seguridad.api.admin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Workeando20.erp.seguridad.dto.PermisoUsuarioDTO;
import Workeando20.erp.seguridad.model.Permiso;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import Workeando20.erp.seguridad.service.UsuarioPermisoService;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioPermisoController {

    private final UsuarioPermisoService usuarioPermisoService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AdminUsuarioPermisoController(UsuarioPermisoService usuarioPermisoService,
            UsuarioRepository usuarioRepository) {
        this.usuarioPermisoService = usuarioPermisoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{usuarioId}/permisos")
    public ResponseEntity<?> asignarPermisoAUsuario(@PathVariable Long usuarioId,
            @RequestBody Map<String, Long> request) {
        try {
            Long permisoId = request.get("permisoId");
            if (permisoId == null) {
                return ResponseEntity.badRequest().body("El ID del permiso es requerido");
            }

            Usuario usuarioActualizado = usuarioPermisoService.asignarPermisoAUsuario(usuarioId, permisoId);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @DeleteMapping("/{usuarioId}/permisos/{permisoId}")
    public ResponseEntity<?> revocarPermisoDeUsuario(@PathVariable Long usuarioId, @PathVariable Long permisoId) {
        try {
            Usuario usuarioActualizado = usuarioPermisoService.revocarPermisoDeUsuario(usuarioId, permisoId);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @GetMapping("/{usuarioId}/permisos")
    public ResponseEntity<List<PermisoUsuarioDTO>> obtenerPermisosDeUsuario(@PathVariable Long usuarioId) {
        try {
            List<PermisoUsuarioDTO> permisos = usuarioPermisoService.obtenerPermisosConHeredado(usuarioId);
            return ResponseEntity.ok(permisos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{usuarioId}/permisos-directos")
    public ResponseEntity<Set<Permiso>> obtenerPermisosDirectosDeUsuario(@PathVariable Long usuarioId) {
        try {
            Set<Permiso> permisos = usuarioPermisoService.obtenerPermisosDirectosDeUsuario(usuarioId);
            return ResponseEntity.ok(permisos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
