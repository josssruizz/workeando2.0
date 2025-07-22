package Workeando20.erp.dominio.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "modalidad")
    private String modalidad;

    private String ubicacion;

    @Column(name = "monto_propuesto")
    private BigDecimal montoPropuesto;

    @Size(max = 20)
    @Column(name = "modalidad_pago")
    private String modalidadPago;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_final")
    private LocalDate fechaFinal;

    @Column(name = "estado")
     private String estado;

    @NotNull
    @Column(name = "id_empleador", nullable = false)
    private Long idEmpleador;

    // NUEVOS CAMPOS
    @Column(name = "id_freelancer_contratado")
    private Long idFreelancerContratado;

    @Column(name = "pagado")
    @Builder.Default
    private Boolean pagado = false;

    @Column(name = "calificado_empleador")
    @Builder.Default
    private Boolean calificadoEmpleador = false;

    @Column(name = "calificado_freelancer")
    @Builder.Default
    private Boolean calificadoFreelancer = false;
}
