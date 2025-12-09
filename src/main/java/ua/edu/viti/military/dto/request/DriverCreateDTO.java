package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateDTO {

    @NotBlank(message = "Військовий квиток обов'язковий")
    @Size(max = 50)
    private String militaryId;

    @NotBlank(message = "Ім'я обов'язкове")
    private String firstName;

    @NotBlank(message = "Прізвище обов'язкове")
    private String lastName;

    private String middleName;

    @Size(max = 50)
    private String rank;

    @NotBlank(message = "Номер прав обов'язковий")
    @Size(max = 50)
    private String licenseNumber;

    @NotBlank(message = "Категорії прав обов'язкові")
    private String licenseCategories;

    @Future(message = "Термін дії прав має бути в майбутньому")
    private LocalDate licenseExpiryDate;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Телефон має бути у форматі +380XXXXXXXXX")
    private String phoneNumber;

    private Boolean isActive = true; // За замовчуванням водій активний
}