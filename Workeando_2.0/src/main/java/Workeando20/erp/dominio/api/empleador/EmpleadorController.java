package Workeando20.erp.dominio.api.empleador;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Workeando20.erp.dominio.dto.PostulacionDTO;
import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.dto.UsuarioDTO;
import Workeando20.erp.dominio.model.Categoria;
import Workeando20.erp.dominio.service.ServicioPostulacion;
import Workeando20.erp.dominio.service.ServicioProyecto;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleador")
public class EmpleadorController {

    @Autowired
    private ServicioProyecto servicioProyecto;

    @Autowired
    private ServicioPostulacion servicioPostulacion;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String mostrarDashboard(Model model,
            Principal principal,
            @RequestParam(value = "proyectosPage", defaultValue = "0") int proyectosPage,
            @RequestParam(value = "postulacionesPage", defaultValue = "0") int postulacionesPage,
            @RequestParam(value = "gestionPage", defaultValue = "0") int gestionPage, // Nuevo parámetro de paginación
            @RequestParam(value = "tab", defaultValue = "proyectos") String tab,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "categoriaIdPostulaciones", required = false) Long categoriaIdPostulaciones,
            @RequestParam(value = "proyectoId", required = false) Long proyectoId) {

        // 1) Usuario autenticado
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario autenticado"));
        model.addAttribute("usuarioId", usuario.getId());

        // 2) Proyectos paginados (con o sin filtro de categoría)
        PageRequest proyectosPg = PageRequest.of(proyectosPage, 6);
        Page<ProyectoDTO> proyectosPageObj = (categoriaId != null)
                ? servicioProyecto.obtenerProyectosPaginadosPorCategoria(categoriaId, proyectosPg)
                : servicioProyecto.obtenerProyectosPaginados(proyectosPg);
        List<ProyectoDTO> proyectos = proyectosPageObj.getContent();

        // 3) Postulaciones paginadas: SIEMPRE todas cuando tab="postulantes"
        PageRequest postulacionesPg = PageRequest.of(postulacionesPage, 6);
        Page<PostulacionDTO> postulacionesPageObj;
        if ("postulantes".equals(tab)) {
            // pestaña de postulaciones recientes: todas las postulaciones
            postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPaginadas(postulacionesPg);
        } else if (proyectoId != null) {
            // al hacer click en “Ver postulantes” de un proyecto concreto
            postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPorProyecto(proyectoId, postulacionesPg);
        } else if (categoriaIdPostulaciones != null) {
            // filtro de postulaciones por categoría
            postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPaginadasPorCategoria(categoriaIdPostulaciones, postulacionesPg);
        } else {
            // fallback (por si tab cambió o no hay filtros)
            postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPaginadas(postulacionesPg);
        }
        List<PostulacionDTO> postulaciones = postulacionesPageObj.getContent();

        // 4) Proyectos en gestión (contratados)
        PageRequest gestionPg = PageRequest.of(gestionPage, 6);
        Page<ProyectoDTO> proyectosGestionPageObj = servicioProyecto.obtenerProyectosContratadosPorEmpleador(usuario.getId(), gestionPg);
        List<ProyectoDTO> proyectosGestion = proyectosGestionPageObj.getContent();


        // 5) Otras colecciones y DTOs
        List<Categoria> categorias = servicioProyecto.obtenerCategorias();
        UsuarioDTO empleador = new UsuarioDTO();
        empleador.setNombre(usuario.getNombre());
        ProyectoDTO proyectoSeleccionado = (proyectoId != null)
                ? servicioProyecto.obtenerProyectoPorId(proyectoId)
                : null;

        // 6) Pongo todo en el modelo para Thymeleaf
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("proyectosCurrentPage", proyectosPage);
        model.addAttribute("proyectosTotalPages", proyectosPageObj.getTotalPages());
        model.addAttribute("proyectosTotalElements", proyectosPageObj.getTotalElements());

        model.addAttribute("postulaciones", postulaciones);
        model.addAttribute("postulacionesCurrentPage", postulacionesPage);
        model.addAttribute("postulacionesTotalPages", postulacionesPageObj.getTotalPages());
        model.addAttribute("postulacionesTotalElements", postulacionesPageObj.getTotalElements());

        model.addAttribute("proyectosGestion", proyectosGestion); // Añadir proyectos en gestión
        model.addAttribute("gestionCurrentPage", gestionPage);
        model.addAttribute("gestionTotalPages", proyectosGestionPageObj.getTotalPages());
        model.addAttribute("gestionTotalElements", proyectosGestionPageObj.getTotalElements());


        model.addAttribute("empleador", empleador);
        model.addAttribute("categorias", categorias);
        model.addAttribute("proyectoDTO", new ProyectoDTO());
        model.addAttribute("activeTab", tab);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("categoriaIdPostulaciones", categoriaIdPostulaciones);
        model.addAttribute("proyectoId", proyectoId);
        model.addAttribute("proyectoSeleccionado", proyectoSeleccionado);

        return "empleador";
    }

    // Nuevo endpoint REST para obtener proyecto por id
    @GetMapping("/proyecto/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProyectoPorId(@PathVariable Long id) {
        ProyectoDTO proyectoDTO = servicioProyecto.obtenerProyectoPorId(id);
        if (proyectoDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proyectoDTO);
    }

    @PostMapping("/proyectos")
    public String crearProyecto(@Valid @ModelAttribute("proyectoDTO") ProyectoDTO proyectoDTO,
            BindingResult result,
            Model model,
            Principal principal) {

        if (result.hasErrors()) {
            // Datos para recargar el dashboard con el modal si hay errores
            Page<ProyectoDTO> proyectosPageObj = servicioProyecto.obtenerProyectosPaginados(PageRequest.of(0, 4));
            List<ProyectoDTO> proyectos = proyectosPageObj.getContent();

            Page<PostulacionDTO> postulacionesPageObj = servicioPostulacion.obtenerPostulacionesPaginadas(PageRequest.of(0, 10));
            List<PostulacionDTO> postulaciones = postulacionesPageObj.getContent();

            List<Categoria> categorias = servicioProyecto.obtenerCategorias();

            // Atributos para proyectos
            model.addAttribute("proyectos", proyectos);
            model.addAttribute("proyectosCurrentPage", 0);
            model.addAttribute("proyectosTotalPages", proyectosPageObj.getTotalPages());
            model.addAttribute("proyectosTotalElements", proyectosPageObj.getTotalElements());

            // Atributos para postulaciones
            model.addAttribute("postulaciones", postulaciones);
            model.addAttribute("postulacionesCurrentPage", 0);
            model.addAttribute("postulacionesTotalPages", postulacionesPageObj.getTotalPages());
            model.addAttribute("postulacionesTotalElements", postulacionesPageObj.getTotalElements());

            model.addAttribute("categorias", categorias);
            model.addAttribute("proyectoDTO", proyectoDTO); // para mantener datos ingresados
            model.addAttribute("activeTab", "proyectos");

            return "empleador"; // vuelve al dashboard con errores
        }

        Usuario usuario = usuarioRepository.findByCorreo(principal.getName()).orElseThrow();
        // CORRECCIÓN: Asignar el ID del usuario directamente, no el ID del perfil.
        // El campo id_empleador en Proyecto se refiere al ID del Usuario.
        proyectoDTO.setIdEmpleador(usuario.getId()); 

        servicioProyecto.crearProyecto(proyectoDTO);
        return "redirect:/empleador";
    }

    @PostMapping("/proyectos/{id}/cerrar")
    public String cerrarProyecto(@PathVariable Long id) {
        servicioProyecto.cerrarProyecto(id);
        return "redirect:/empleador";
    }

    @GetMapping("/postulaciones/{id}/aceptar")
    public String aceptarGet(@PathVariable Long id,
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "proyectoId", required = false) Long proyectoId) {
        servicioPostulacion.aceptarPostulacion(id);
        return "redirect:/empleador?tab=" + (tab != null ? tab : "postulantes")
                + (proyectoId != null ? "&proyectoId=" + proyectoId : "");
    }

    @GetMapping("/postulaciones/{id}/rechazar")
    public String rechazarGet(@PathVariable Long id,
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "proyectoId", required = false) Long proyectoId) {
        servicioPostulacion.rechazarPostulacion(id);
        return "redirect:/empleador?tab=" + (tab != null ? tab : "postulantes")
                + (proyectoId != null ? "&proyectoId=" + proyectoId : "");
    }

    @GetMapping("/categorias")
    @ResponseBody
    public List<Categoria> obtenerCategorias() {
        return servicioProyecto.obtenerCategorias();
    }

    // NUEVOS ENDPOINTS
    @PostMapping("/proyectos/{id}/marcar-pagado")
    public ResponseEntity<?> marcarProyectoComoPagado(@PathVariable Long id) {
        try {
            servicioProyecto.marcarProyectoComoPagado(id);
            return ResponseEntity.ok().body("Proyecto marcado como pagado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al marcar proyecto como pagado.");
        }
    }

    @PostMapping("/proyectos/{id}/calificar-freelancer")
    public ResponseEntity<?> calificarFreelancer(@PathVariable Long id) {
        try {
            servicioProyecto.marcarProyectoComoCalificado(id, "EMPLEADOR");
            return ResponseEntity.ok().body("Freelancer calificado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al calificar freelancer.");
        }
    }
}
