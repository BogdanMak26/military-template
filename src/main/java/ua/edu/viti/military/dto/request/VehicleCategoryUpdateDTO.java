package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCategoryUpdateDTO {

    @Size(max = 100, message = "Назва занадто довга")
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 20)
    private String requiredLicense;

    @Positive(message = "Вантажопідйомність має бути позитивною")
    private Integer maxLoadCapacity;
}