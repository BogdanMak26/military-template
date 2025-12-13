package ua.edu.viti.military.mapper;

import org.mapstruct.*; // Важливо імпортувати все
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.request.VehicleCategoryUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleCategoryMapper {

    VehicleCategoryResponseDTO toDTO(VehicleCategory entity);
    List<VehicleCategoryResponseDTO> toDTOList(List<VehicleCategory> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VehicleCategory toEntity(VehicleCategoryCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(VehicleCategoryUpdateDTO dto, @MappingTarget VehicleCategory entity);
}