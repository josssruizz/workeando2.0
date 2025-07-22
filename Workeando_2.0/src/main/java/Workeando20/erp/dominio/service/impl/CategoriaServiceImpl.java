package Workeando20.erp.dominio.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Workeando20.erp.dominio.model.Categoria;
import Workeando20.erp.dominio.repository.CategoriaRepository;
import Workeando20.erp.dominio.service.CategoriaService;

@Service
@Transactional("dominioTransactionManager")
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_CATEGORIAS')")
    public Categoria crearCategoria(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        Categoria nuevaCategoria = Categoria.builder()
                .nombre(nombre.trim())
                .build();
        
        try {
            return categoriaRepository.save(nuevaCategoria);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + nombre);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_CATEGORIAS')")
    public void eliminarCategoria(Long id) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        
        if (categoriaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la categoría con ID: " + id);
        }
        
        categoriaRepository.delete(categoriaOptional.get());
    }

    @Override
    @PreAuthorize("hasAuthority('GESTIONAR_CATEGORIAS')")
    public Categoria actualizarCategoria(Long id, String nuevoNombre) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);
        
        if (categoriaOptional.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la categoría con ID: " + id);
        }
        
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        Categoria categoria = categoriaOptional.get();
        categoria.setNombre(nuevoNombre.trim());
        
        try {
            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + nuevoNombre);
        }
    }
}
