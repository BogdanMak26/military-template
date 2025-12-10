package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.validation.OnUpdate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для редагування категорії техніки")
public class VehicleCategoryUpdateDTO {

    @Size(max = 100, message = "Назва занадто довга", groups = OnUpdate.class)
    @Schema(example = "Легковий автомобіль")
    private String name;

    @Size(max = 500, groups = OnUpdate.class)
    @Schema(example = "Транспорт для перевезення особового складу")
    private String description;

    @Size(max = 20, groups = OnUpdate.class)
    @Schema(example = "B")
    private String requiredLicense;

    @Positive(message = "Вантажопідйомність має бути позитивною", groups = OnUpdate.class)
    @Schema(example = "500")
    private Integer maxLoadCapacity;
}