package Workeando20.erp.dominio.api.empleador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import Workeando20.erp.dominio.dto.PerfilEmpleadorDTO;
import Workeando20.erp.dominio.model.PerfilEmpleador;
import Workeando20.erp.dominio.service.impl.PerfilServiceImpl;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/perfil/empleador")
@RequiredArgsConstructor
public class PerfilEmpleadorController {

    private final PerfilServiceImpl perfilService;

    @GetMapping("/{usuarioId}")
    public String mostrarPerfil(@PathVariable Long usuarioId, Model model) {
        try {
            PerfilEmpleador perfil = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
            model.addAttribute("perfil", perfil);
            return "empleador-perfil";
        } catch (RuntimeException e) {
            return "empleador-perfil";
        }
    }

    @GetMapping("/{usuarioId}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long usuarioId, Model model) {
        try {
            PerfilEmpleador perfil = perfilService.obtenerPerfilPorUsuarioId(usuarioId);
            model.addAttribute("perfil", perfil);
            return "empleador-editar-perfil";
        } catch (RuntimeException e) {
            return "empleador-editar-perfil";
        }
    }

    @PostMapping("/{usuarioId}/actualizar")
    public String actualizarPerfil(@PathVariable Long usuarioId,
                                   @Valid @ModelAttribute("perfil") PerfilEmpleadorDTO perfilDTO,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "empleador-editar-perfil";
        }
        try {
            PerfilEmpleador perfilActualizado = mapToEntity(perfilDTO);
            perfilActualizado.setUsuarioId(usuarioId);
            perfilService.actualizarPerfil(usuarioId, perfilActualizado);
            return "redirect:/perfil/empleador/" + usuarioId;
        } catch (RuntimeException e) {
            model.addAttribute("error", "Perfil no encontrado");
            return "empleador-perfil";
        }
    }

    private PerfilEmpleador mapToEntity(PerfilEmpleadorDTO dto) {
        return PerfilEmpleador.builder()
                .tipoEmpleador(dto.getTipoEmpleador())
                .nombrePublico(dto.getNombrePublico())
                .descripcion(dto.getDescripcion())
                .acercaDeNosotros(dto.getAcercaDeNosotros())
                .build();
    }
}
