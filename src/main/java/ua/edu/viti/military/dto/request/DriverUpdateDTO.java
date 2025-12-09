package ua.edu.viti.military.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateDTO {

    // Ім'я та прізвище можуть змінюватися (помилка при введенні або зміна прізвища)
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    private String middleName;

    @Size(max = 50)
    private String rank; // Наприклад: зміна з "Солдат" на "Сержант"

    @Size(max = 50)
    private String licenseNumber; // Якщо отримав нове посвідчення

    private String licenseCategories; // Якщо відкрив нову категорію

    @Future(message = "Термін дії прав має бути в майбутньому")
    private LocalDate licenseExpiryDate;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Телефон має бути у форматі +380XXXXXXXXX")
    private String phoneNumber;

    private Boolean isActive; // Важливо: можливість "звільнити" водія (soft delete)
}