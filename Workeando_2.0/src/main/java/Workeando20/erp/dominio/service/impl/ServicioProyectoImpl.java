package Workeando20.erp.dominio.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.mapper.ProyectoMapper;
import Workeando20.erp.dominio.model.Categoria;
import Workeando20.erp.dominio.model.Proyecto;
import Workeando20.erp.dominio.repository.CategoriaRepository;
import Workeando20.erp.dominio.repository.PerfilEmpleadorRepository;
import Workeando20.erp.dominio.repository.PerfilFreelancerRepository;
import Workeando20.erp.dominio.repository.PostulacionRepository;
import Workeando20.erp.dominio.repository.ProyectoRepository;
import Workeando20.erp.dominio.service.ServicioProyecto;
import Workeando20.erp.seguridad.repository.UsuarioRepository;


@Service
public class ServicioProyectoImpl implements ServicioProyecto {
    private final ProyectoRepository proyectoRepository;
    private final ProyectoMapper proyectoMapper;
    private final PostulacionRepository postulacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository; 
    private final PerfilFreelancerRepository perfilFreelancerRepository; 
    private final PerfilEmpleadorRepository perfilEmpleadorRepository; 

    private static final Logger logger = LoggerFactory.getLogger(ServicioProyectoImpl.class); // Cambiado a ServicioProyectoImpl.class

    @Autowired
    public ServicioProyectoImpl(ProyectoRepository proyectoRepository, ProyectoMapper proyectoMapper,
                                PostulacionRepository postulacionRepository, CategoriaRepository categoriaRepository,
                                UsuarioRepository usuarioRepository, PerfilFreelancerRepository perfilFreelancerRepository,
                                PerfilEmpleadorRepository perfilEmpleadorRepository) {
        this.proyectoRepository = proyectoRepository;
        this.proyectoMapper = proyectoMapper;
        this.postulacionRepository = postulacionRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilFreelancerRepository = perfilFreelancerRepository;
        this.perfilEmpleadorRepository = perfilEmpleadorRepository;
    }

    @Override
    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public Page<ProyectoDTO> listarProyectosParaFreelancer(Long categoriaId, Long freelancerId, Pageable pageable) {
        List<Long> proyectosPostulados = List.of();
        if (freelancerId != null) {
            proyectosPostulados = obtenerIdsProyectosPostuladosPorFreelancer(freelancerId);
            logger.debug("Freelancer {} ha postulado a los proyectos: {}", freelancerId, proyectosPostulados);
        }

        Page<Proyecto> proyectosPage;
        String estado = "ABIERTO";

        if (proyectosPostulados.isEmpty()) {
            if (categoriaId == null) {
                proyectosPage = proyectoRepository.findAllByEstado(estado, pageable);
                logger.debug("Buscando todos los proyectos ABIERTOS.");
            } else {
                proyectosPage = proyectoRepository.findByCategoriaIdAndEstado(categoriaId, estado, pageable);
                logger.debug("Buscando proyectos ABIERTOS por categoría {}.", categoriaId);
            }
        } else {
            if (categoriaId == null) {
                proyectosPage = proyectoRepository.findByIdNotInAndEstado(proyectosPostulados, estado, pageable);
                logger.debug("Buscando proyectos ABIERTOS no postulados por freelancer {}.", freelancerId);
            } else {
                proyectosPage = proyectoRepository.findByCategoriaIdAndIdNotInAndEstado(categoriaId, proyectosPostulados, estado, pageable);
                logger.debug("Buscando proyectos ABIERTOS por categoría {} y no postulados por freelancer {}.", categoriaId, freelancerId);
            }
        }

        logger.info("Proyectos disponibles encontrados: {}", proyectosPage.getTotalElements());
        proyectosPage.forEach(p -> logger.debug("Proyecto disponible: {} - {}", p.getId(), p.getTitulo()));

        return proyectosPage.map(proyectoMapper::toDTO);
    }

    private List<Long> obtenerIdsProyectosPostuladosPorFreelancer(Long freelancerId) {
        return postulacionRepository.findByIdFreelancer(freelancerId).stream()
                .map(postulacion -> postulacion.getProyecto().getId())
                .toList();
    }

    @Override
    public Page<ProyectoDTO> obtenerProyectosPaginadosPorCategoria(Long categoriaId, Pageable pageable) {
        logger.debug("Obteniendo proyectos paginados por categoría: {}", categoriaId);
        var proyectosPage = proyectoRepository.findByCategoriaIdAndEstadoOrEstadoIsNull(categoriaId, "ABIERTO", pageable);
        return proyectosPage.map(proyectoMapper::toDTO);
    }

    @Override
    public ProyectoDTO obtenerProyectoPorId(Long id) {
        logger.debug("Obteniendo proyecto por ID: {}", id);
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);
        if (proyecto == null) {
            logger.debug("Proyecto con ID {} no encontrado.", id);
            return null;
        }
        ProyectoDTO dto = proyectoMapper.toDTO(proyecto);
        // Cargar datos del freelancer contratado si existe
        if (proyecto.getIdFreelancerContratado() != null) {
            usuarioRepository.findById(proyecto.getIdFreelancerContratado()).ifPresent(freelancerUser -> {
                dto.setFreelancerContratadoNombre(freelancerUser.getNombre());
                // Obtener datos de contacto del perfil del freelancer
                perfilFreelancerRepository.findByUsuarioId(freelancerUser.getId()).ifPresent(perfil -> {
                    dto.setFreelancerContratadoCorreo(freelancerUser.getCorreo());
                    dto.setFreelancerContratadoTelefono(perfil.getTelefono());
                });
            });
        }
        // Cargar datos del empleador si existe (para el caso del freelancer viendo su proyecto contratado)
        usuarioRepository.findById(proyecto.getIdEmpleador()).ifPresent(empleadorUser -> {
            dto.setEmpleadorNombre(empleadorUser.getNombre());
            perfilEmpleadorRepository.findByUsuarioId(empleadorUser.getId()).ifPresent(perfil -> {
                dto.setEmpleadorCorreo(empleadorUser.getCorreo());
                // Asumiendo que el perfil del empleador tiene un campo de teléfono si es necesario
                // dto.setEmpleadorTelefono(perfil.getTelefono());
            });
        });
        return dto;
    }

    @Override
    public Page<ProyectoDTO> obtenerProyectosContratadosPorFreelancer(Long freelancerId, Pageable pageable) {
        logger.debug("Obteniendo proyectos CONTRATADOS para freelancer ID: {}", freelancerId);
        Page<Proyecto> proyectosPage = proyectoRepository.findByIdFreelancerContratadoAndEstado(freelancerId, "CONTRATADO", pageable);
        return proyectosPage.map(proyecto -> {
            ProyectoDTO dto = proyectoMapper.toDTO(proyecto);
            // Cargar datos del empleador
            usuarioRepository.findById(proyecto.getIdEmpleador()).ifPresent(empleadorUser -> {
                dto.setEmpleadorNombre(empleadorUser.getNombre());
                perfilEmpleadorRepository.findByUsuarioId(empleadorUser.getId()).ifPresent(perfil -> {
                    dto.setEmpleadorCorreo(empleadorUser.getCorreo());
                    // Asumiendo que el perfil del empleador tiene un campo de teléfono si es necesario
                    // dto.setEmpleadorTelefono(perfil.getTelefono());
                });
            });
            return dto;
        });
    }

    @Override
    public Page<ProyectoDTO> listarProyectosPorEmpleador(Long categoriaId, Long empleadorId, Pageable pageable) {
        Page<Proyecto> proyectosPage;
        String estado = "ABIERTO";

        logger.debug("Listando proyectos ABIERTOS para empleador ID: {}", empleadorId);
        if (categoriaId != null) {
            proyectosPage = proyectoRepository.findByIdEmpleadorAndCategoriaIdAndEstado(empleadorId, categoriaId, estado, pageable);
            logger.debug("Filtrando por categoría: {}", categoriaId);
        } else {
            proyectosPage = proyectoRepository.findByIdEmpleadorAndEstado(empleadorId, estado, pageable);
        }

        logger.info("Proyectos del empleador {} encontrados: {}", empleadorId, proyectosPage.getTotalElements());
        proyectosPage.forEach(p -> logger.debug("Proyecto del empleador: {} - {}", p.getId(), p.getTitulo()));

        return proyectosPage.map(proyectoMapper::toDTO);
    }

    @Override
    public Page<ProyectoDTO> obtenerProyectosPaginados(PageRequest pageRequest) {
        logger.debug("Obteniendo proyectos paginados con estado ABIERTO.");
        Page<Proyecto> proyectosPage = proyectoRepository.findAllByEstadoOrEstadoIsNull("ABIERTO", pageRequest);
        return proyectosPage.map(proyectoMapper::toDTO);
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void crearProyecto(ProyectoDTO proyectoDTO) {
        logger.info("Creando nuevo proyecto: {}", proyectoDTO.getTitulo());
        Proyecto proyecto = proyectoMapper.toEntity(proyectoDTO);
        proyecto.setEstado("ABIERTO"); // Estado inicial
        proyectoRepository.save(proyecto);
        logger.debug("Proyecto {} guardado con ID: {}", proyecto.getTitulo(), proyecto.getId());
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void cerrarProyecto(Long id) {
        logger.info("Cerrando proyecto con ID: {}", id);
        Proyecto proyecto = proyectoRepository.findById(id).orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyecto.setEstado("CERRADO");
        proyectoRepository.save(proyecto);
        logger.debug("Proyecto {} marcado como CERRADO.", id);
    }

    // NUEVOS MÉTODOS
    @Override
    public Page<ProyectoDTO> obtenerProyectosContratadosPorEmpleador(Long empleadorId, Pageable pageable) {
        logger.info("Buscando proyectos CONTRATADOS para empleador ID: {}", empleadorId);
        Page<Proyecto> proyectosPage = proyectoRepository.findByIdEmpleadorAndEstadoAndIdFreelancerContratadoIsNotNull(empleadorId, "CONTRATADO", pageable);
        logger.info("Proyectos CONTRATADOS encontrados para empleador {}: {}", empleadorId, proyectosPage.getTotalElements());
        return proyectosPage.map(proyecto -> {
            ProyectoDTO dto = proyectoMapper.toDTO(proyecto);
            // Cargar datos del freelancer contratado
            if (proyecto.getIdFreelancerContratado() != null) {
                usuarioRepository.findById(proyecto.getIdFreelancerContratado()).ifPresent(freelancerUser -> {
                    dto.setFreelancerContratadoNombre(freelancerUser.getNombre());
                    perfilFreelancerRepository.findByUsuarioId(freelancerUser.getId()).ifPresent(perfil -> {
                        dto.setFreelancerContratadoCorreo(freelancerUser.getCorreo());
                        dto.setFreelancerContratadoTelefono(perfil.getTelefono());
                    });
                });
            }
            return dto;
        });
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void marcarProyectoComoPagado(Long proyectoId) {
        logger.info("Marcando proyecto {} como PAGADO.", proyectoId);
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyecto.setPagado(true);
        proyectoRepository.save(proyecto);
        logger.debug("Proyecto {} marcado como PAGADO exitosamente.", proyectoId);
    }

    @Override
    @Transactional("dominioTransactionManager")
    public void marcarProyectoComoCalificado(Long proyectoId, String rolCalificador) {
        logger.info("Marcando proyecto {} como CALIFICADO por rol: {}", proyectoId, rolCalificador);
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if ("EMPLEADOR".equalsIgnoreCase(rolCalificador)) {
            proyecto.setCalificadoEmpleador(true);
        } else if ("FREELANCER".equalsIgnoreCase(rolCalificador)) {
            proyecto.setCalificadoFreelancer(true);
        }
        proyectoRepository.save(proyecto);
        logger.debug("Proyecto {} calificado por {}.", proyectoId, rolCalificador);
    }
}
