package Workeando20.erp.dominio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la postulación de un freelancer a un proyecto.
 */
@Entity
@Table(name = "postulacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "id_freelancer", nullable = false)
    private Long idFreelancer;

    @Column(name = "monto_contraoferta", precision = 10, scale = 2)
    private BigDecimal montoContraoferta;

    @Column(name = "fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    @Size(max = 20)
    private String estado;
}
