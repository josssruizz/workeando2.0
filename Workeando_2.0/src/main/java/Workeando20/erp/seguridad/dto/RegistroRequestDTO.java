package Workeando20.erp.seguridad.dto;

import Workeando20.erp.dominio.model.TipoRolRegistro;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroRequestDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private TipoRolRegistro rol;
}
