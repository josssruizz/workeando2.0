package Workeando20.erp.dominio.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostulacionDTO {
    private Long id;
    private String freelancerNombre;
    private Double montoContraoferta;
    private String estado; // PENDIENTE, ACEPTADO, RECHAZADO
    private LocalDateTime fechaPostulacion;
    private ProyectoDTO proyecto;

    public String getFechaPostulacionFormateada() {
        return fechaPostulacion != null ? fechaPostulacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

}
