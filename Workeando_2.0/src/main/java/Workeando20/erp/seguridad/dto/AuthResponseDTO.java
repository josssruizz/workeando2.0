package Workeando20.erp.seguridad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String nombre;
    private String correo;
    private String rol;
}
