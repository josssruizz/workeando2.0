package Workeando20.erp.seguridad.api.auth;

import Workeando20.erp.seguridad.dto.RegistroRequestDTO;
import Workeando20.erp.seguridad.dto.AuthResponseDTO;
import Workeando20.erp.seguridad.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        AuthResponseDTO response = authService.registrar(request);
        return ResponseEntity.ok(response);
    }
}
