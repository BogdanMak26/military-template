package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.request.VehicleCategoryUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.service.VehicleCategoryService;
import ua.edu.viti.military.validation.OnCreate;
import ua.edu.viti.military.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-categories")
@RequiredArgsConstructor
@Tag(name = "1. Категорії техніки", description = "Довідник типів транспорту (Вантажні, Легкові, Гусеничні тощо)")
public class VehicleCategoryController {

    private final VehicleCategoryService categoryService;

    @PostMapping
    @Operation(summary = "Створити нову категорію", description = "Додає новий тип техніки в довідник. Назва має бути унікальною.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Категорію створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації (пуста назва)"),
            @ApiResponse(responseCode = "409", description = "Категорія з такою назвою вже існує")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<VehicleCategoryResponseDTO> create(
            @Validated(OnCreate.class) @RequestBody VehicleCategoryCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Отримати всі категорії", description = "Повертає повний список доступних категорій транспорту.")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<List<VehicleCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати категорію за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішно знайдено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<VehicleCategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити категорію", description = "Змінює назву або опис категорії.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішно оновлено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено"),
            @ApiResponse(responseCode = "409", description = "Нова назва вже зайнята іншою категорією")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<VehicleCategoryResponseDTO> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody VehicleCategoryUpdateDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити категорію", description = "Видаляє категорію з довідника. УВАГА: Це можливо тільки якщо до категорії не прив'язані машини.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Категорію не знайдено"),
            @ApiResponse(responseCode = "409", description = "Неможливо видалити: є прив'язані машини") // SQL Foreign Key error
    })
    @PreAuthorize("hasRole('ADMIN')") // Тільки Адмін має право видаляти довідники
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}