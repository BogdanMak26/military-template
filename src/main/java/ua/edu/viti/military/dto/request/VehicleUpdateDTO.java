package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.FuelType;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.validation.OnUpdate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для оновлення стану транспортного засобу")
public class VehicleUpdateDTO {

    // --- ПАСПОРТНІ ДАНІ (Додані, щоб прибрати попередження MapStruct) ---

    @Schema(description = "Модель техніки", example = "КрАЗ-6322")
    @Size(max = 100)
    private String model;

    @Schema(description = "Номер шасі", example = "CH-1234567890")
    @Size(max = 50)
    private String chassisNumber;

    @Schema(description = "Номер двигуна", example = "ENG-987654321")
    @Size(max = 50)
    private String engineNumber;

    @Schema(description = "Рік випуску", example = "2020")
    @Min(value = 1950)
    private Integer manufactureYear;

    @Schema(description = "Тип пального", example = "DIESEL")
    private FuelType fuelType;

    @Schema(description = "Інтервал між ТО (км)", example = "10000")
    @Positive
    private Integer maintenanceIntervalKm;

    @PositiveOrZero(message = "Пробіг не може бути від'ємним", groups = OnUpdate.class)
    @Schema(description = "Оновлений пробіг", example = "15500")
    private Integer mileage;

    @Schema(description = "Зміна статусу", example = "IN_MAINTENANCE")
    private VehicleStatus status;

    @Schema(description = "Дата проведення ТО (якщо редагується вручну)")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "ID категорії техніки", example = "1")
    private Long categoryId;

    @PositiveOrZero(groups = OnUpdate.class)
    @Schema(description = "Пробіг на момент останнього ТО")
    private Integer lastMaintenanceMileage;

    @Schema(description = "Призначити нового водія (ID)")
    private Long driverId;

    @Positive(groups = OnUpdate.class)
    @Schema(description = "Нова норма витрати пального", example = "12.5")
    private Double fuelConsumption;
}