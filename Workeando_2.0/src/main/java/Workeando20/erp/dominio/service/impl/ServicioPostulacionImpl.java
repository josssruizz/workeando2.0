package Workeando20.erp.dominio.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.dto.PostulacionDTO;
import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.model.Postulacion;
import Workeando20.erp.dominio.model.Proyecto;
import Workeando20.erp.dominio.repository.PostulacionRepository;
import Workeando20.erp.dominio.repository.ProyectoRepository;
import Workeando20.erp.dominio.service.ServicioPostulacion;
import Workeando20.erp.seguridad.model.Usuario;
import Workeando20.erp.seguridad.repository.UsuarioRepository;

@Service
public class ServicioPostulacionImpl implements ServicioPostulacion {

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ServicioPostulacionImpl.class);

    @Override
    public Page<PostulacionDTO> obtenerPostulacionesPaginadas(Pageable pageable) {
        logger.debug("Obteniendo todas las postulaciones paginadas.");
        Page<Postulacion> postulacionesPage = postulacionRepository.findAllWithProyecto(pageable);
        return postulacionesPage.map(this::convertirAPostulacionDTO);
    }

    @Override
    public Page<PostulacionDTO> obtenerPostulacionesPaginadasPorCategoria(Long categoriaId, Pageable pageable) {
        logger.debug("Obteniendo postulaciones paginadas por categoría: {}", categoriaId);
        Page<Postulacion> postulacionesPage = postulacionRepository.findByCategoriaId(categoriaId, pageable);
        return postulacionesPage.map(this::convertirAPostulacionDTO);
    }

    @Override
    public Page<PostulacionDTO> obtenerPostulacionesPorProyecto(Long proyectoId, Pageable pageable) {
        logger.debug("Obteniendo postulaciones por proyecto ID: {}", proyectoId);
        Page<Postulacion> postulacionesPage = postulacionRepository.findByProyectoId(proyectoId, pageable);
        return postulacionesPage.map(this::convertirAPostulacionDTO);
    }

    @Override
    public PostulacionDTO obtenerPostulacionPorId(Long id) {
        logger.debug("Obteniendo postulación por ID: {}", id);
        Postulacion postulacion = postulacionRepository.findById(id).orElse(null);
        if (postulacion == null) {
            logger.debug("Postulación con ID {} no encontrada.", id);
            return null;
        }
        return convertirAPostulacionDTO(postulacion);
    }

    @Override
    @Transactional("dominioTransactionManager") 
    public void aceptarPostulacion(Long id) {
        logger.info("Intentando aceptar postulación con ID: {}", id);
        Postulacion postulacion = postulacionRepository.findById(id).orElse(null);
        if (postulacion != null) {
            postulacion.setEstado("ACEPTADO");
            postulacionRepository.save(postulacion);
            logger.debug("Postulación {} marcada como ACEPTADO.", id);

            Proyecto proyecto = postulacion.getProyecto();
            if (proyecto != null) {
                proyecto.setIdFreelancerContratado(postulacion.getIdFreelancer());
                proyecto.setEstado("CONTRATADO"); // Nuevo estado para proyectos con freelancer aceptado
                proyectoRepository.save(proyecto);
                logger.info("Proyecto {} (ID Empleador: {}) actualizado a estado CONTRATADO con Freelancer ID: {}.",
                            proyecto.getId(), proyecto.getIdEmpleador(), proyecto.getIdFreelancerContratado());
            } else {
                logger.warn("Proyecto asociado a la postulación {} es nulo.", id);
            }
        } else {
            logger.warn("Postulación con ID {} no encontrada para aceptar.", id);
        }
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void rechazarPostulacion(Long id) {
        logger.info("Intentando rechazar postulación con ID: {}", id);
        Postulacion postulacion = postulacionRepository.findById(id).orElse(null);
        if (postulacion != null) {
            postulacion.setEstado("RECHAZADO");
            postulacionRepository.save(postulacion);
            logger.debug("Postulación {} marcada como RECHAZADO.", id);
        } else {
            logger.warn("Postulación con ID {} no encontrada para rechazar.", id);
        }
    }

    @Override
    @Transactional("dominioTransactionManager")
    public PostulacionDTO crearPostulacion(Long proyectoId, Long freelancerId, Double montoContraoferta) {
        logger.info("Creando postulación para Proyecto ID: {} por Freelancer ID: {}", proyectoId, freelancerId);
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElse(null);
        if (proyecto == null) {
            logger.error("Proyecto con ID {} no encontrado para crear postulación.", proyectoId);
            throw new RuntimeException("Proyecto no encontrado");
        }

        // Verificar si ya existe una postulación del freelancer para este proyecto
        List<Postulacion> postulacionesExistentes = postulacionRepository.findByIdFreelancerWithProyecto(freelancerId);
        boolean yaSePostulo = postulacionesExistentes.stream()
                .anyMatch(p -> p.getProyecto().getId().equals(proyectoId));

        if (yaSePostulo) {
            logger.warn("Freelancer {} ya se ha postulado al proyecto {}.", freelancerId, proyectoId);
            throw new RuntimeException("Ya te has postulado a este proyecto anteriormente");
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setProyecto(proyecto);
        postulacion.setIdFreelancer(freelancerId);
        postulacion.setMontoContraoferta(BigDecimal.valueOf(montoContraoferta));
        postulacion.setFechaPostulacion(LocalDateTime.now());
        postulacion.setEstado("PENDIENTE");

        try {
            Postulacion postulacionGuardada = postulacionRepository.save(postulacion);
            logger.debug("Postulación creada con ID: {}", postulacionGuardada.getId());
            return convertirAPostulacionDTO(postulacionGuardada);
        } catch (Exception e) {
            logger.error("Error al crear la postulación para Proyecto ID: {} y Freelancer ID: {}. Causa: {}", proyectoId, freelancerId, e.getMessage());
            // Capturar errores de restricción de unicidad
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                throw new RuntimeException("Ya te has postulado a este proyecto anteriormente");
            }
            throw new RuntimeException("Error al crear la postulación: " + e.getMessage());
        }
    }

    @Override
    public List<PostulacionDTO> obtenerPostulacionesPorFreelancer(Long freelancerId) {
        logger.debug("Obteniendo postulaciones para freelancer ID: {}", freelancerId);
        List<Postulacion> postulaciones = postulacionRepository.findByIdFreelancerWithProyecto(freelancerId);
        return postulaciones.stream()
                .map(this::convertirAPostulacionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PostulacionDTO> obtenerPostulacionesPaginadasPorFreelancerYCategoria(Long freelancerId, Long categoriaId, Pageable pageable) {
        logger.debug("Obteniendo postulaciones paginadas para freelancer {} y categoría {}.", freelancerId, categoriaId);
        Page<Postulacion> postulacionesPage = postulacionRepository.findByIdFreelancerAndCategoriaId(freelancerId, categoriaId, pageable);
        return postulacionesPage.map(this::convertirAPostulacionDTO);
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void eliminarPostulacion(Long id) {
        logger.info("Eliminando postulación con ID: {}", id);
        postulacionRepository.deleteById(id);
        logger.debug("Postulación {} eliminada exitosamente.", id);
    }

    private PostulacionDTO convertirAPostulacionDTO(Postulacion postulacion) {
        PostulacionDTO dto = new PostulacionDTO();
        dto.setId(postulacion.getId());
        if (postulacion.getIdFreelancer() != null) {
            Usuario usuario = usuarioRepository.findById(postulacion.getIdFreelancer()).orElse(null);
            if (usuario != null) {
                dto.setFreelancerNombre(usuario.getNombre());
            } else {
                dto.setFreelancerNombre("Desconocido");
            }
        } else {
            dto.setFreelancerNombre("Desconocido");
        }
        dto.setMontoContraoferta(postulacion.getMontoContraoferta() != null ? postulacion.getMontoContraoferta().doubleValue() : 0.0);
        dto.setEstado(postulacion.getEstado());
        dto.setFechaPostulacion(postulacion.getFechaPostulacion());
        
        // Convertir proyecto a DTO
        if (postulacion.getProyecto() != null) {
            ProyectoDTO proyectoDTO = new ProyectoDTO();
            proyectoDTO.setId(postulacion.getProyecto().getId());
            proyectoDTO.setTitulo(postulacion.getProyecto().getTitulo());
            proyectoDTO.setDescripcion(postulacion.getProyecto().getDescripcion());
            proyectoDTO.setMontoPropuesto(postulacion.getProyecto().getMontoPropuesto());
            proyectoDTO.setEstado(postulacion.getProyecto().getEstado()); // Asegúrate de pasar el estado del proyecto
            dto.setProyecto(proyectoDTO);
        }
        
        return dto;
    }
}
