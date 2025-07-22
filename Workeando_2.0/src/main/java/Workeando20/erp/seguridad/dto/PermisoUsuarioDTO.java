package Workeando20.erp.seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermisoUsuarioDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean heredado;
}
