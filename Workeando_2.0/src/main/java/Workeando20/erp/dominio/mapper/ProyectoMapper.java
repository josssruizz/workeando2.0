package Workeando20.erp.dominio.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import Workeando20.erp.dominio.dto.ProyectoDTO;
import Workeando20.erp.dominio.model.Proyecto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProyectoMapper {

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "ubicacion", ignore = true)
    @Mapping(target = "modalidadPago", ignore = true)
    Proyecto toEntity(ProyectoDTO dto);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "postulaciones", ignore = true)
    ProyectoDTO toDTO(Proyecto proyecto);
}
