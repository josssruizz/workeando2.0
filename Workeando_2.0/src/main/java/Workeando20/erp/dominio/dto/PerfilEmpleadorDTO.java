package Workeando20.erp.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos del perfil empleador.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilEmpleadorDTO {

    @NotBlank(message = "El tipo de empleador es obligatorio")
    @Size(max = 20, message = "El tipo de empleador debe tener máximo 20 caracteres")
    private String tipoEmpleador;

    @NotBlank(message = "El nombre público es obligatorio")
    @Size(max = 100, message = "El nombre público debe tener máximo 100 caracteres")
    private String nombrePublico;

    private String descripcion;

    private String acercaDeNosotros;
}
