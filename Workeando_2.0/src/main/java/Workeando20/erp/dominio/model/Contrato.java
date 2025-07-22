package Workeando20.erp.dominio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa el contrato generado tras una conversación entre freelancer y empleador.
 */
@Entity
@Table(name = "contrato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conversacion", nullable = false)
    private Conversacion conversacion;

    @Column(name = "archivo_pdf", columnDefinition = "TEXT")
    private String archivoPdf;

    @Size(max = 30)
    private String estado;

    @Column(name = "firmado_freelancer", nullable = false)
    @Builder.Default
    private Boolean firmadoFreelancer = false;

    @Column(name = "firmado_empleador", nullable = false)
    @Builder.Default
    private Boolean firmadoEmpleador = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
