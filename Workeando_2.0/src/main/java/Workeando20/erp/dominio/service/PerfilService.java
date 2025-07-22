package Workeando20.erp.dominio.service;

import Workeando20.erp.dominio.model.TipoRolRegistro;

public interface PerfilService {

    /**
     * Crea un perfil inicial dependiendo del tipo de rol registrado (freelancer o empleador).
     * Este método debe usarse después de crear el usuario en el módulo de seguridad.
     *
     * @param rol        Tipo de rol seleccionado por el usuario
     * @param usuarioId  ID del usuario creado
     */
    void crearPerfilInicial(TipoRolRegistro rol, Long usuarioId);
}
