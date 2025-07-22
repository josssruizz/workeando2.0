package Workeando20.erp.seguridad.service;

import Workeando20.erp.seguridad.dto.AuthResponseDTO;
import Workeando20.erp.seguridad.dto.RegistroRequestDTO;

public interface AuthService {
    AuthResponseDTO registrar(RegistroRequestDTO request);
}
