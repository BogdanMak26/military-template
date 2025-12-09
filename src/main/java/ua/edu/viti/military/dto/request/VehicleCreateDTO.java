package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.FuelType;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.validation.OnCreate;
import ua.edu.viti.military.validation.OnUpdate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для реєстрації нового транспортного засобу")
public class VehicleCreateDTO {

    @Schema(description = "Модель техніки", example = "КрАЗ-6322", required = true)
    @NotBlank(groups = OnCreate.class, message = "Модель обов'язкова")
    @Size(max = 100)
    private String model;

    @Schema(description = "Державний номерний знак (унікальний)", example = "0532 А1", required = true)
    @NotBlank(groups = OnCreate.class, message = "Номерний знак обов'язковий")
    @Size(max = 20)
    private String registrationNumber;

    @Schema(description = "ID категорії транспорту", example = "1", required = true)
    @NotNull(groups = OnCreate.class)
    @Positive
    private Long categoryId;

    @Schema(description = "Номер двигуна", example = "ENG-88421-22")
    @Size(max = 50)
    private String engineNumber;

    @Schema(description = "Номер шасі (VIN)", example = "Y6D12345678901234")
    @Size(max = 50)
    private String chassisNumber;

    @Schema(description = "Рік випуску", example = "2015")
    @Min(value = 1950)
    private Integer manufactureYear;

    @Schema(description = "Поточний пробіг (км)", example = "12500", required = true)
    @NotNull(groups = OnCreate.class)
    @PositiveOrZero
    private Integer mileage;

    @Schema(description = "Тип пального", example = "DIESEL", required = true)
    @NotNull(groups = OnCreate.class)
    private FuelType fuelType;

    @Schema(description = "Витрата пального (л/100км)", example = "35.5")
    @Positive
    private Double fuelConsumption;

    @Schema(description = "Інтервал між ТО (км)", example = "10000", required = true)
    @NotNull(groups = OnCreate.class)
    @Positive
    private Integer maintenanceIntervalKm;

    @Schema(description = "Дата останнього ТО", example = "2024-01-15")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "Пробіг на момент останнього ТО", example = "10000")
    private Integer lastMaintenanceMileage;

    @Schema(description = "ID закріпленого водія (опційно)", example = "5")
    private Long driverId;

    @Schema(description = "Поточний статус", example = "OPERATIONAL", required = true)
    @NotNull(groups = OnCreate.class)
    private VehicleStatus status;
}