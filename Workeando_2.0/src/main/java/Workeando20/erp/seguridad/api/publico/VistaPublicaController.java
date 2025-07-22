package Workeando20.erp.seguridad.api.publico;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import Workeando20.erp.seguridad.dto.RegistroRequestDTO;
import Workeando20.erp.seguridad.service.AuthService;

@Controller
public class VistaPublicaController {

    private final AuthService authService;

    public VistaPublicaController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String redireccionInicial() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("registroRequestDTO", new RegistroRequestDTO());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute RegistroRequestDTO dto, Model model) {
        try {
            authService.registrar(dto);
            return "redirect:/login?registrado";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }
    }

    @GetMapping("/403")
    public String accesoDenegado() {
        return "403";
    }

}
