package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.validation.OnCreate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для створення категорії техніки")
public class VehicleCategoryCreateDTO {

    @NotBlank(groups = OnCreate.class, message = "Назва категорії обов'язкова")
    @Size(max = 100, message = "Назва занадто довга", groups = OnCreate.class)
    @Schema(example = "Вантажний автомобіль")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Код категорії обов'язковий")
    @Size(max = 50, message = "Код занадто довгий", groups = OnCreate.class)
    @Schema(example = "TRUCK_HEAVY")
    private String code;

    @Size(max = 500, groups = OnCreate.class)
    @Schema(example = "Техніка для перевезення вантажів понад 5 тонн")
    private String description;

    @Size(max = 20, message = "Категорія прав має бути короткою (напр. 'C')", groups = OnCreate.class)
    @Schema(example = "C")
    private String requiredLicense;

    @Positive(message = "Вантажопідйомність має бути позитивною", groups = OnCreate.class)
    @Schema(example = "10000")
    private Integer maxLoadCapacity;
}