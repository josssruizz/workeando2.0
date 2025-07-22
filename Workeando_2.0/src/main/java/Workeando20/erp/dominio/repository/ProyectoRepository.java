package Workeando20.erp.dominio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import Workeando20.erp.dominio.model.Proyecto;


@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

@EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findAllByEstado(String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByCategoriaIdAndEstado(Long categoriaId, String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByCategoriaIdAndIdNotInAndEstado(Long categoriaId, List<Long> ids, String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByIdNotInAndEstado(List<Long> ids, String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByIdEmpleadorAndEstado(Long idEmpleador, String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByIdEmpleadorAndCategoriaIdAndEstado(Long idEmpleador, Long categoriaId, String estado, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE (p.categoria.id = :categoriaId AND p.estado = :estado) OR (p.categoria.id = :categoriaId AND p.estado IS NULL)")
    Page<Proyecto> findByCategoriaIdAndEstadoOrEstadoIsNull(@Param("categoriaId") Long categoriaId, @Param("estado") String estado, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.estado = :estado OR p.estado IS NULL")
    Page<Proyecto> findAllByEstadoOrEstadoIsNull(@Param("estado") String estado, Pageable pageable);

    // NUEVOS MÉTODOS
    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByIdEmpleadorAndEstadoAndIdFreelancerContratadoIsNotNull(Long idEmpleador, String estado, Pageable pageable);

    @EntityGraph(attributePaths = {"categoria"})
    Page<Proyecto> findByIdFreelancerContratadoAndEstado(Long idFreelancerContratado, String estado, Pageable pageable);
}
