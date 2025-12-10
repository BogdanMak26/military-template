package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.validation.OnUpdate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для оновлення стану транспортного засобу")
public class VehicleUpdateDTO {

    @PositiveOrZero(message = "Пробіг не може бути від'ємним", groups = OnUpdate.class)
    @Schema(description = "Оновлений пробіг", example = "15500")
    private Integer mileage;

    @Schema(description = "Зміна статусу (наприклад, відправка на ремонт)", example = "IN_MAINTENANCE")
    private VehicleStatus status;

    @Schema(description = "Дата проведення ТО (якщо редагується вручну)")
    private LocalDate lastMaintenanceDate;

    @PositiveOrZero(groups = OnUpdate.class)
    @Schema(description = "Пробіг на момент останнього ТО")
    private Integer lastMaintenanceMileage;

    @Schema(description = "Призначити нового водія (ID)")
    private Long driverId;

    @Positive(groups = OnUpdate.class)
    @Schema(description = "Нова норма витрати пального", example = "12.5")
    private Double fuelConsumption;
}