package ua.edu.viti.military.mapper;
import org.mapstruct.*;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    DriverResponseDTO toDTO(Driver entity);
    List<DriverResponseDTO> toDTOList(List<Driver> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true) // Ставимо в сервісі
    Driver toEntity(DriverCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "militaryId", ignore = true) // Військовий квиток не змінюється
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(DriverUpdateDTO dto, @MappingTarget Driver entity);
}