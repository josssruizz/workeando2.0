package Workeando20.erp.dominio.api.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import Workeando20.erp.dominio.service.CategoriaService;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import Workeando20.erp.seguridad.service.RolService;
import Workeando20.erp.seguridad.service.UsuarioPermisoService;

@Controller
public class AdminController {

    private final RolService rolService;
    private final UsuarioPermisoService usuarioPermisoService;
    private final CategoriaService categoriaService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AdminController(RolService rolService, UsuarioPermisoService usuarioPermisoService, 
                          CategoriaService categoriaService, UsuarioRepository usuarioRepository) {
        this.rolService = rolService;
        this.usuarioPermisoService = usuarioPermisoService;
        this.categoriaService = categoriaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        try {
            // Cargar datos para el panel de administración
            System.out.println("Cargando roles...");
            model.addAttribute("roles", rolService.obtenerTodosLosRoles());
            
            System.out.println("Cargando permisos...");
            model.addAttribute("permisos", usuarioPermisoService.obtenerTodosLosPermisos());
            
            System.out.println("Cargando categorías...");
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            
            System.out.println("Cargando usuarios...");
            model.addAttribute("usuarios", usuarioRepository.findAll());
            
            System.out.println("Datos cargados exitosamente");
        } catch (Exception e) {
            // En caso de error, continuar con el panel básico
            System.err.println("Error al cargar datos del panel: " + e.getMessage());
            model.addAttribute("error", "Error al cargar datos del panel: " + e.getMessage());
        }
        
        return "admin";
    }
}
