package Workeando20.erp.dominio.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.model.Categoria;

public interface ServicioProyecto {

    List<Categoria> obtenerCategorias();

    Page<ProyectoDTO> listarProyectosParaFreelancer(Long categoriaId, Long freelancerId, Pageable pageable);

    Page<ProyectoDTO> obtenerProyectosPaginadosPorCategoria(Long categoriaId, Pageable pageable);

    ProyectoDTO obtenerProyectoPorId(Long id);

    Page<ProyectoDTO> obtenerProyectosContratadosPorFreelancer(Long freelancerId, Pageable pageable);
    Page<ProyectoDTO> listarProyectosPorEmpleador(Long categoriaId, Long empleadorId, Pageable pageable);
    Page<ProyectoDTO> obtenerProyectosPaginados(PageRequest pageRequest);

    void crearProyecto(ProyectoDTO proyectoDTO);

    void cerrarProyecto(Long proyectoId);

    Page<ProyectoDTO> obtenerProyectosContratadosPorEmpleador(Long empleadorId, Pageable pageable);
    void marcarProyectoComoPagado(Long proyectoId);
    void marcarProyectoComoCalificado(Long proyectoId, String rolCalificador);
}
