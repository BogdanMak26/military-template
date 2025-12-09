package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.VehicleStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateDTO {
    // В UpdateDTO ми прибираємо поля, які не можна змінювати (наприклад, chassisNumber, registrationNumber)
    // Або залишаємо тільки ті, що часто змінюються під час експлуатації

    @PositiveOrZero(message = "Пробіг не може бути від'ємним")
    private Integer mileage;

    private VehicleStatus status;

    private LocalDate lastMaintenanceDate;

    @PositiveOrZero
    private Integer lastMaintenanceMileage;

    private Long driverId; // Можливість змінити водія (або зняти його, якщо null)

    @Positive
    private Double fuelConsumption; // Наприклад, після ремонту змінилась витрата
}