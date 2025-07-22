package Workeando20.erp.dominio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "perfil_freelancer", uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PerfilFreelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Formato de teléfono inválido")
    private String telefono;

    @Size(max = 2000, message = "El campo Acerca de mí no puede exceder 2000 caracteres")
    @Column(name = "acerca_de_mi", columnDefinition = "TEXT")
    private String acercaDeMi;

    @Size(max = 2000, message = "Las habilidades no pueden exceder 2000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String habilidades;

    @Size(max = 1000, message = "El portafolio no puede exceder 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String portafolio;
}
