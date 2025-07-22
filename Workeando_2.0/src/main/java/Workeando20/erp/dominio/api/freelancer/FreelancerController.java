package Workeando20.erp.dominio.api.freelancer;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.model.Categoria;
import Workeando20.erp.dominio.model.PerfilFreelancer;
import Workeando20.erp.dominio.repository.PerfilFreelancerRepository;
import Workeando20.erp.dominio.service.PerfilFreelancerService;
import Workeando20.erp.dominio.service.ServicioPostulacion;
import Workeando20.erp.dominio.service.ServicioProyecto;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;

@Controller
public class FreelancerController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilFreelancerService perfilFreelancerService;
    private final ServicioProyecto servicioProyecto;
    private final ServicioPostulacion servicioPostulacion;
    private PerfilFreelancerRepository perfilFreelancerRepository;

    public FreelancerController(UsuarioRepository usuarioRepository, PerfilFreelancerService perfilFreelancerService, ServicioProyecto servicioProyecto, ServicioPostulacion servicioPostulacion, PerfilFreelancerRepository perfilFreelancerRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilFreelancerService = perfilFreelancerService;
        this.servicioProyecto = servicioProyecto;
        this.servicioPostulacion = servicioPostulacion;
        this.perfilFreelancerRepository = perfilFreelancerRepository;
    }

    @PostMapping("/api/freelancer/postulacion/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarPostulacion(
            @RequestParam("postulacionId") Long postulacionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Usuario no autenticado"));
        }
        @SuppressWarnings("unused")
        Usuario usuario = usuarioOpt.get();

        try {
            servicioPostulacion.eliminarPostulacion(postulacionId);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Postulación eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Error al eliminar la postulación"));
        }
    }

    @GetMapping("/freelancer")
    public String freelancerDashboard(
            Model model,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "proyectosPage", defaultValue = "0") int proyectosPage,
            @RequestParam(value = "postulacionesPage", defaultValue = "0") int postulacionesPage,
            @RequestParam(value = "gestionPage", defaultValue = "0") int gestionPage) { // Nuevo parámetro de paginación

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioOpt.get();

        // Cargar categorías dinámicamente desde la base de datos
        List<Categoria> categorias = servicioProyecto.obtenerCategorias();
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriaId", categoriaId);

        // Cargar postulaciones del freelancer con paginación y filtro por categoría
        var postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPaginadasPorFreelancerYCategoria(usuario.getId(), categoriaId, PageRequest.of(postulacionesPage, 3));
        var postulaciones = postulacionesPageObj.getContent();
        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("postulacionesPage", postulacionesPageObj);
        model.addAttribute("postulacionesCurrentPage", postulacionesPage);
        model.addAttribute("postulacionesTotalPages", postulacionesPageObj.getTotalPages());

        // Cargar proyectos paginados filtrados por categoría y excluyendo los postulados directamente en la consulta
        var proyectosPageObj = servicioProyecto.listarProyectosParaFreelancer(categoriaId, usuario.getId(), PageRequest.of(proyectosPage, 3));
        var proyectos = proyectosPageObj.getContent();

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("proyectosCurrentPage", proyectosPage);
        model.addAttribute("proyectosTotalPages", proyectosPageObj.getTotalPages());

        // Cargar proyectos en gestión (contratados)
        PageRequest gestionPg = PageRequest.of(gestionPage, 6);
        Page<ProyectoDTO> proyectosGestionPageObj = servicioProyecto.obtenerProyectosContratadosPorFreelancer(usuario.getId(), gestionPg);
        List<ProyectoDTO> proyectosGestion = proyectosGestionPageObj.getContent();
        model.addAttribute("proyectosGestion", proyectosGestion);
        model.addAttribute("gestionCurrentPage", gestionPage);
        model.addAttribute("gestionTotalPages", proyectosGestionPageObj.getTotalPages());

        return "freelancer";
    }

    @GetMapping("/api/proyectos")
    @ResponseBody
    public Page<ProyectoDTO> obtenerProyectosPaginadosApi(
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        return servicioProyecto.obtenerProyectosPaginadosPorCategoria(categoriaId, PageRequest.of(page, 3));
    }

    // Nuevo endpoint REST para obtener proyecto por id
    @GetMapping("/freelancer/proyecto/{id}")
    public ResponseEntity<?> obtenerProyectoPorId(@PathVariable Long id) {
        ProyectoDTO proyectoDTO = servicioProyecto.obtenerProyectoPorId(id);
        if (proyectoDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proyectoDTO);
    }

    @GetMapping("/freelancer/perfil")
    public String freelancerProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioOpt.get();

        // Usar el ID del usuario autenticado para obtener el perfil
        Optional<PerfilFreelancer> perfilOpt = perfilFreelancerService.obtenerPerfilPorUsuarioId(usuario.getId());
        PerfilFreelancer perfil = perfilOpt.orElse(new PerfilFreelancer());

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        // Asegúrate de que estos atributos se pasen al modelo
        model.addAttribute("habilidadesPersonalizadas", perfil.getHabilidades() != null ? perfil.getHabilidades() : "");
        model.addAttribute("acercaDeMi", perfil.getAcercaDeMi() != null ? perfil.getAcercaDeMi() : "");

        // Contar cuántos empleadores han calificado a este freelancer
        // Se asume que un proyecto con idFreelancerContratado y calificadoEmpleador = true
        // representa una calificación de un empleador.
        long calificacionesEmpleadores = servicioProyecto.obtenerProyectosContratadosPorFreelancer(usuario.getId(), PageRequest.of(0, Integer.MAX_VALUE))
                .getContent().stream()
                .filter(ProyectoDTO::getCalificadoEmpleador)
                .count();
        model.addAttribute("calificacionesEmpleadores", calificacionesEmpleadores);

        model.addAttribute("postulaciones", servicioPostulacion.obtenerPostulacionesPorFreelancer(usuario.getId()));
        // Modificado para usar el nuevo método paginado, aunque aquí se usa sin paginación explícita
        model.addAttribute("proyectosContratados", servicioProyecto.obtenerProyectosContratadosPorFreelancer(usuario.getId(), PageRequest.of(0, 100)).getContent()); // Obtener todos para el perfil

        return "freelancer-perfil";
    }

    @GetMapping("/freelancer/perfil/editar")
    public String mostrarFormularioEdicion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();

        // Usar el ID del usuario autenticado para obtener el perfil
        Optional<PerfilFreelancer> perfilOpt = perfilFreelancerRepository.findByUsuarioId(usuario.getId());
        PerfilFreelancer perfil = perfilOpt.orElse(new PerfilFreelancer());

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        // Eliminar experienciaStr
        // model.addAttribute("experienciaStr", perfil.getExperiencia());
        // model.addAttribute("habilidadesStr", perfil.getHabilidades());

        // Asegúrate de que estos atributos se pasen al modelo
        // model.addAttribute("experienciaPersonalizada", perfil.getExperiencia() != null ? perfil.getExperiencia() : ""); // Eliminar
        model.addAttribute("habilidadesPersonalizadas", perfil.getHabilidades() != null ? perfil.getHabilidades() : "");
        model.addAttribute("acercaDeMi", perfil.getAcercaDeMi() != null ? perfil.getAcercaDeMi() : "");

        return "freelancer-editar-perfil-nuevo";
    }

    @PostMapping("/freelancer/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("telefono") String telefono,
            // @RequestParam(name = "experienciaPersonalizada", required = false) String experienciaPersonalizada, // Eliminar
            @RequestParam(name = "acercaDeMi", required = false) String acercaDeMi,
            @RequestParam(name = "habilidadesPersonalizadas", required = false) String habilidadesPersonalizadas,
            @RequestParam("portafolio") String portafolio,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioOpt.get();

        Optional<PerfilFreelancer> perfilOpt = perfilFreelancerService.obtenerPerfilPorUsuarioId(usuario.getId());
        PerfilFreelancer perfil = perfilOpt.orElse(new PerfilFreelancer());

        // Manejar experiencias personalizadas (directamente desde el formulario)
        // String experienciaFinal = (experienciaPersonalizada != null && !experienciaPersonalizada.trim().isEmpty()) // Eliminar
        //         ? experienciaPersonalizada.trim()
        //         : "";
        // Manejar habilidades personalizadas (directamente desde el formulario)
        String habilidadesFinal = (habilidadesPersonalizadas != null && !habilidadesPersonalizadas.trim().isEmpty())
                ? habilidadesPersonalizadas.trim()
                : "";

        // Manejar otros campos
        String portafolioFinal = (portafolio != null && !portafolio.trim().isEmpty())
                ? portafolio.trim()
                : (perfil.getPortafolio() != null ? perfil.getPortafolio() : "");

        String acercaDeMiFinal = (acercaDeMi != null && !acercaDeMi.trim().isEmpty())
                ? acercaDeMi.trim()
                : (perfil.getAcercaDeMi() != null ? perfil.getAcercaDeMi() : "");

        // Actualizar la llamada al servicio
        perfilFreelancerService.actualizarPerfilConAcercaDeMi(usuario.getId(), telefono, null, habilidadesFinal, portafolioFinal, acercaDeMiFinal); // Pasar null para experiencia

        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente");
        return "redirect:/freelancer/perfil";
    }

    @PostMapping("/freelancer/postular")
    public String postularseAProyecto(
            @RequestParam("proyectoId") Long proyectoId,
            @RequestParam("montoContraoferta") Double montoContraoferta,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioOpt.get();

        try {
            // Validar monto mínimo
            if (montoContraoferta == null || montoContraoferta <= 0) {
                redirectAttributes.addFlashAttribute("error", "El monto de contraoferta debe ser mayor a 0");
                return "redirect:/freelancer";
            }

            servicioPostulacion.crearPostulacion(proyectoId, usuario.getId(), montoContraoferta);
            redirectAttributes.addFlashAttribute("mensaje", "Postulación enviada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al enviar la postulación");
        }

        return "redirect:/freelancer";
    }

    @PostMapping("/api/freelancer/postular")
    @ResponseBody
    public ResponseEntity<?> postularseAProyectoAjax(
            @RequestParam("proyectoId") Long proyectoId,
            @RequestParam("montoContraoferta") Double montoContraoferta) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String correo = authentication.getName();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Usuario no autenticado"));
        }
        Usuario usuario = usuarioOpt.get();

        try {
            // Validar monto mínimo
            if (montoContraoferta == null || montoContraoferta <= 0) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "El monto de contraoferta debe ser mayor a 0"));
            }

            servicioPostulacion.crearPostulacion(proyectoId, usuario.getId(), montoContraoferta);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "Postulación enviada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Error inesperado al enviar la postulación"));
        }
    }

    public PerfilFreelancerRepository getPerfilFreelancerRepository() {
        return perfilFreelancerRepository;
    }

    public void setPerfilFreelancerRepository(PerfilFreelancerRepository perfilFreelancerRepository) {
        this.perfilFreelancerRepository = perfilFreelancerRepository;
    }

    // NUEVOS ENDPOINTS
    @PostMapping("/proyectos/{id}/calificar-empleador")
    public ResponseEntity<?> calificarEmpleador(@PathVariable Long id) {
        try {
            servicioProyecto.marcarProyectoComoCalificado(id, "FREELANCER");
            return ResponseEntity.ok().body("Empleador calificado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al calificar empleador.");
        }
    }
}
