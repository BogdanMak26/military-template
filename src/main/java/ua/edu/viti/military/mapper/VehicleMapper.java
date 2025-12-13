package ua.edu.viti.military.mapper;

import org.mapstruct.*;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.Vehicle;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {VehicleCategoryMapper.class, DriverMapper.class})
public interface VehicleMapper {

    VehicleResponseDTO toDTO(Vehicle entity);
    List<VehicleResponseDTO> toDTOList(List<Vehicle> entities);

    // Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastMaintenanceDate", ignore = true)
    @Mapping(target = "lastMaintenanceMileage", constant = "0")
    @Mapping(target = "status", ignore = true) // Статус ставимо в сервісі
    Vehicle toEntity(VehicleCreateDTO dto);

    // Update
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // <--- Ігноруємо
    @Mapping(target = "updatedAt", ignore = true) // <--- Ігноруємо
    @Mapping(target = "lastMaintenanceDate", ignore = true)
    @Mapping(target = "lastMaintenanceMileage", ignore = true)
    @Mapping(target = "fuelConsumption", ignore = true) // Якщо його немає в DTO
    @Mapping(target = "mileage", ignore = true) // Пробіг оновлюємо окремим методом

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(VehicleUpdateDTO dto, @MappingTarget Vehicle entity);
}