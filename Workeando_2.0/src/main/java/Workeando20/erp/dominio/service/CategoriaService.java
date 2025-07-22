package Workeando20.erp.dominio.service;

import java.util.List;
import java.util.Optional;

import Workeando20.erp.dominio.model.Categoria;

public interface CategoriaService {
    
    List<Categoria> obtenerTodasLasCategorias();
    
    Optional<Categoria> obtenerCategoriaPorId(Long id);
    
    Categoria crearCategoria(String nombre);
    
    void eliminarCategoria(Long id);
    
    Categoria actualizarCategoria(Long id, String nuevoNombre);
}
