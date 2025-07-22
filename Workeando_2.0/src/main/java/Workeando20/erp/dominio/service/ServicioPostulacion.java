package Workeando20.erp.dominio.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import Workeando20.erp.dominio.dto.PostulacionDTO;

import java.util.List;

public interface ServicioPostulacion {
    Page<PostulacionDTO> obtenerPostulacionesPaginadas(Pageable pageable);
    Page<PostulacionDTO> obtenerPostulacionesPaginadasPorCategoria(Long categoriaId, Pageable pageable);
    Page<PostulacionDTO> obtenerPostulacionesPorProyecto(Long proyectoId, Pageable pageable);
    PostulacionDTO obtenerPostulacionPorId(Long id);
    void aceptarPostulacion(Long id);
    void rechazarPostulacion(Long id);
    PostulacionDTO crearPostulacion(Long proyectoId, Long freelancerId, Double montoContraoferta);
    List<PostulacionDTO> obtenerPostulacionesPorFreelancer(Long freelancerId);
    Page<PostulacionDTO> obtenerPostulacionesPaginadasPorFreelancerYCategoria(Long freelancerId, Long categoriaId, Pageable pageable);

    void eliminarPostulacion(Long id);
}
