package Workeando20.erp.dominio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProyectoDTO {
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String titulo;

    @NotNull(message = "El empleador es obligatorio")
    private Long idEmpleador;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El monto propuesto es obligatorio")
    @DecimalMin(value = "10.0", message = "El presupuesto mínimo es de S/.10")
    private BigDecimal montoPropuesto;

    @Pattern(regexp = "VIRTUAL|PRESENCIAL|HIBRIDO", message = "Modalidad inválida")
    private String modalidad;

    @NotNull(message = "Debe seleccionar una categoría")
    private Integer categoriaId;

    // Campos adicionales del modelo Proyecto
    private String ubicacion;
    
    @Pattern(regexp = "POR_HORAS|FIJO|POR_PROYECTO", message = "Modalidad de pago inválida")
    private String modalidadPago;

    private LocalDate fechaInicio;
    private LocalDate fechaFinal;

    // Información de la categoría
    private String categoriaNombre;

    private List<PostulacionDTO> postulaciones;

    private Integer postulantesCount; // Nuevo campo para contar postulantes

    // Nuevos campos para la vista de proyectos contratados
    private String empleadorNombre;
    private String estado;
    private Long contratoId;

    // NUEVOS CAMPOS PARA GESTION
    private Long idFreelancerContratado;
    private String freelancerContratadoNombre;
    private String freelancerContratadoCorreo;
    private String freelancerContratadoTelefono;
    private Boolean pagado;
    private Boolean calificadoEmpleador; // Este campo es clave para el conteo
    private Boolean calificadoFreelancer;
    private String empleadorCorreo;
    private String empleadorTelefono;


    public String getFechaInicioFormateada() {
        return fechaInicio != null ? fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getFechaFinalFormateada() {
        return fechaFinal != null ? fechaFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    // Método para obtener el texto de modalidad de pago formateado
    public String getModalidadPagoFormateada() {
        if (modalidadPago == null) return "";
        return switch (modalidadPago) {
            case "POR_HORAS" -> "Por Horas";
            case "FIJO" -> "Pago Fijo";
            case "POR_PROYECTO" -> "Por Proyecto";
            default -> modalidadPago;
        };
    }

    // Método para obtener el texto de modalidad formateado
    public String getModalidadFormateada() {
        if (modalidad == null) return "";
        return switch (modalidad) {
            case "VIRTUAL" -> "Virtual";
            case "PRESENCIAL" -> "Presencial";
            case "HIBRIDO" -> "Híbrido";
            default -> modalidad;
        };
    }
}
