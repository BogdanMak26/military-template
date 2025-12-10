package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.validation.OnUpdate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для оновлення інформації про водія")
public class DriverUpdateDTO {

    @Size(min = 2, max = 50, groups = OnUpdate.class)
    @Schema(example = "Олексій")
    private String firstName;

    @Size(min = 2, max = 50, groups = OnUpdate.class)
    @Schema(example = "Коваленко")
    private String lastName;

    @Schema(example = "Петрович")
    private String middleName;

    @Size(max = 50, groups = OnUpdate.class)
    @Schema(example = "Старший сержант")
    private String rank;

    @Size(max = 50, groups = OnUpdate.class)
    @Schema(description = "Новий номер посвідчення (якщо змінився)", example = "BXT 112233")
    private String licenseNumber;

    @Schema(description = "Оновлені категорії", example = "B, C, D")
    private String licenseCategories;

    @Future(message = "Термін дії прав має бути в майбутньому", groups = OnUpdate.class)
    @Schema(example = "2032-05-20")
    private LocalDate licenseExpiryDate;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Телефон має бути у форматі +380XXXXXXXXX", groups = OnUpdate.class)
    @Schema(example = "+380970000000")
    private String phoneNumber;

    @Schema(description = "Статус водія (false = звільнений/неактивний)")
    private Boolean isActive;
}