package Workeando20.erp.dominio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil_empleador", uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PerfilEmpleador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Size(max = 20)
    @Column(name = "tipo_empleador", nullable = false)
    @Builder.Default
    private String tipoEmpleador = "PERSONA";

    @Size(max = 100)
    @Column(name = "nombre_publico", nullable = false)
    private String nombrePublico;

    @Column(name = "acerca_de_nosotros", columnDefinition = "TEXT")
    private String acercaDeNosotros;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Nuevos campos
    @Size(max = 100)
    @Column(name = "correo_contacto")
    private String correoContacto;

    @Size(max = 20)
    @Column(name = "telefono_contacto")
    private String telefonoContacto;
}
