package Workeando20.erp.dominio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Workeando20.erp.dominio.model.Postulacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {
    List<Postulacion> findByIdFreelancer(Long idFreelancer);

    @Query("SELECT p FROM Postulacion p JOIN FETCH p.proyecto WHERE p.idFreelancer = :idFreelancer")
    List<Postulacion> findByIdFreelancerWithProyecto(@Param("idFreelancer") Long idFreelancer);

    @Query(value = "SELECT p FROM Postulacion p JOIN FETCH p.proyecto",
           countQuery = "SELECT count(p) FROM Postulacion p")
    Page<Postulacion> findAllWithProyecto(Pageable pageable);

    @Query(value = "SELECT p FROM Postulacion p JOIN FETCH p.proyecto pr WHERE (:categoriaId IS NULL OR pr.categoria.id = :categoriaId)",
           countQuery = "SELECT count(p) FROM Postulacion p WHERE (:categoriaId IS NULL OR p.proyecto.categoria.id = :categoriaId)")
   Page<Postulacion> findByCategoriaId(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query(value = "SELECT p FROM Postulacion p JOIN FETCH p.proyecto WHERE p.proyecto.id = :proyectoId",
           countQuery = "SELECT count(p) FROM Postulacion p WHERE p.proyecto.id = :proyectoId")
    Page<Postulacion> findByProyectoId(@Param("proyectoId") Long proyectoId, Pageable pageable);

    @Query(value = "SELECT p FROM Postulacion p JOIN FETCH p.proyecto pr WHERE p.idFreelancer = :idFreelancer AND (:categoriaId IS NULL OR pr.categoria.id = :categoriaId)",
           countQuery = "SELECT count(p) FROM Postulacion p WHERE p.idFreelancer = :idFreelancer AND (:categoriaId IS NULL OR p.proyecto.categoria.id = :categoriaId)")
    Page<Postulacion> findByIdFreelancerAndCategoriaId(@Param("idFreelancer") Long idFreelancer, @Param("categoriaId") Long categoriaId, Pageable pageable);
}
