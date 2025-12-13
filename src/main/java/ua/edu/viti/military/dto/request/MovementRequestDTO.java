package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.edu.viti.military.entity.MovementType;

@Data
public class MovementRequestDTO {
    @NotNull
    private Long vehicleId;

    private Long driverId; // Обов'язкове тільки для ASSIGN_DRIVER

    @NotNull
    private MovementType type;

    private String notes;
    private Integer currentMileage; // Для оновлення пробігу
}