package Workeando20.erp.seguridad.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.service.PerfilService;
import Workeando20.erp.seguridad.dto.AuthResponseDTO;
import Workeando20.erp.seguridad.dto.RegistroRequestDTO;
import Workeando20.erp.seguridad.model.Rol;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.RolRepository;
import Workeando20.erp.seguridad.repository.UsuarioRepository;
import Workeando20.erp.seguridad.security.JwtService;
import Workeando20.erp.seguridad.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final PerfilService perfilService; // ← Servicio de dominio

    @Override
    @Transactional("seguridadTransactionManager")
    public AuthResponseDTO registrar(RegistroRequestDTO request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Rol rol = rolRepository.findByNombre(request.getRol().name())
                .orElseThrow(() -> new RuntimeException("Rol no válido"));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .activo(true)
                .build();
        usuario.addRol(rol);
        usuarioRepository.save(usuario);

        // Crear perfil en la otra BD
        perfilService.crearPerfilInicial(request.getRol(), usuario.getId());

        String token = jwtService.generarToken(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(rol.getNombre())
                .build();
    }
}
