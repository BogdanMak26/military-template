package ua.edu.viti.military.dto.response;

import lombok.Builder;
import lombok.Data;
import ua.edu.viti.military.entity.MovementType;
import java.time.LocalDateTime;

@Data
@Builder
public class MovementResponseDTO {
    private Long id;
    private String vehicleNumber;
    private String driverName; // Наприклад "Сержант Петренко"
    private MovementType type;
    private Integer mileage;
    private String notes;
    private LocalDateTime performedAt;
}