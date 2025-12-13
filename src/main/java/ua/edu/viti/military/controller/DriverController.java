package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.service.DriverService;
import ua.edu.viti.military.validation.OnCreate;
import ua.edu.viti.military.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "3. Водії", description = "Управління особовим складом (водіями)")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Реєстрація нового водія", description = "Додає нового водія в базу даних. Перевіряє унікальність військового квитка та прав.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Водія успішно створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації"),
            @ApiResponse(responseCode = "409", description = "Водій з такими документами вже існує")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<DriverResponseDTO> create(
            @Validated(OnCreate.class) @RequestBody DriverCreateDTO dto) {
        log.info("Request to create driver: {}", dto.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Отримати список активних водіїв", description = "Повертає список всіх водіїв, які не звільнені.")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<List<DriverResponseDTO>> getAllActive() {
        return ResponseEntity.ok(driverService.getAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Знайти водія за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успішно знайдено"),
            @ApiResponse(responseCode = "404", description = "Водія не знайдено")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити дані водія", description = "Дозволяє змінити звання, класність або прізвище.")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<DriverResponseDTO> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody DriverUpdateDTO dto) {
        return ResponseEntity.ok(driverService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Звільнити/Видалити водія", description = "Ця операція доступна ТІЛЬКИ АДМІНІСТРАТОРУ.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успішно видалено"),
            @ApiResponse(responseCode = "404", description = "Водія не знайдено")
    })
    @PreAuthorize("hasRole('ADMIN')") // <-- Тільки Адмін!
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}