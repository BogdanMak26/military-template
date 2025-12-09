package ua.edu.viti.military.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data                  // Генерує геттери, сеттери, toString
@NoArgsConstructor     // Генерує пустий конструктор
@AllArgsConstructor    // Генерує конструктор з усіма полями (виправляє вашу помилку)
@Builder               // Додає паттерн Builder (рекомендовано)
public class VehicleCategoryResponseDTO {

    private Long id;
    private String name;
    private String code;
    private String description;

    // Специфічні поля для Варіанту B
    private String requiredLicense;
    private Integer maxLoadCapacity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}