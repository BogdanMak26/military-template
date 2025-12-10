package ua.edu.viti.military.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.validation.OnCreate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дані для реєстрації нового водія")
public class DriverCreateDTO {

    @NotBlank(groups = OnCreate.class, message = "Військовий квиток обов'язковий")
    @Size(max = 50, groups = OnCreate.class)
    @Schema(example = "АА 123456")
    private String militaryId;

    @NotBlank(groups = OnCreate.class, message = "Ім'я обов'язкове")
    @Schema(example = "Іван")
    private String firstName;

    @NotBlank(groups = OnCreate.class, message = "Прізвище обов'язкове")
    @Schema(example = "Петренко")
    private String lastName;

    @Schema(example = "Іванович")
    private String middleName;

    @Size(max = 50, groups = OnCreate.class)
    @Schema(example = "Сержант")
    private String rank;

    @NotBlank(groups = OnCreate.class, message = "Номер прав обов'язковий")
    @Size(max = 50, groups = OnCreate.class)
    @Schema(example = "BXT 998877")
    private String licenseNumber;

    @NotBlank(groups = OnCreate.class, message = "Категорії прав обов'язкові")
    @Schema(example = "B, C")
    private String licenseCategories;

    @Future(groups = OnCreate.class, message = "Термін дії прав має бути в майбутньому")
    @Schema(example = "2030-01-01")
    private LocalDate licenseExpiryDate;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Телефон має бути у форматі +380XXXXXXXXX", groups = OnCreate.class)
    @Schema(example = "+380971234567")
    private String phoneNumber;

    @Schema(description = "Чи активний водій", example = "true")
    private Boolean isActive = true;
}