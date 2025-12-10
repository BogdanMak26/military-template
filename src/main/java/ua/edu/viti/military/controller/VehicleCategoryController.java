package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
@Tag(name = "1. Категорії транспорту", description = "Управління типами техніки (вантажівки, легкові, БТР тощо)")
public class VehicleCategoryController {

    private final VehicleCategoryService categoryService;

    @PostMapping
    @Operation(summary = "Створити нову категорію")
    public ResponseEntity<VehicleCategoryResponseDTO> create(
            @Validated(OnCreate.class) @RequestBody VehicleCategoryCreateDTO dto) {
        log.info("REST request to create category: {}", dto);
        VehicleCategoryResponseDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати категорію за ID")
    public ResponseEntity<VehicleCategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Отримати всі категорії")
    public ResponseEntity<List<VehicleCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити категорію")
    public ResponseEntity<VehicleCategoryResponseDTO> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody VehicleCategoryUpdateDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити категорію")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build(); // Повертає статус 204 No Content
    }
}