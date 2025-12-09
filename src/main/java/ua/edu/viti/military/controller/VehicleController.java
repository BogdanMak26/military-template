package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.service.VehicleService;
import ua.edu.viti.military.validation.OnCreate;
import ua.edu.viti.military.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "3. Транспорт (Vehicles)", description = "Головний контролер для управління технікою, пробігом та ТО")
public class VehicleController {

    private final VehicleService vehicleService;


    @PostMapping
    @Operation(
            summary = "Зареєструвати новий транспорт",
            description = "Створює нову картку автомобіля. Перевіряє унікальність держ. номера та наявність категорії."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Транспорт успішно створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації (некоректні дані)"),
            @ApiResponse(responseCode = "409", description = "Транспорт з таким номером вже існує")
    })
    public ResponseEntity<VehicleResponseDTO> create(
            @Validated(OnCreate.class) @RequestBody VehicleCreateDTO dto) { // <-- Використовуємо групу OnCreate
        log.info("REST request to create vehicle: {}", dto.getRegistrationNumber());
        VehicleResponseDTO created = vehicleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Отримати транспорт за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Знайдено"),
            @ApiResponse(responseCode = "404", description = "Транспорт не знайдено")
    })
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Отримати весь список", description = "Дозволяє фільтрувати техніку за статусом (справна, в ремонті тощо)")
    public ResponseEntity<List<VehicleResponseDTO>> getAll(
            @Parameter(description = "Статус техніки (опційно)")
            @RequestParam(required = false) VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.getAll(status));
    }


    @GetMapping("/requiring-maintenance")
    @Operation(
            summary = "⚠️ Техніка, що потребує ТО",
            description = "Повертає список машин, де різниця між поточним пробігом і останнім ТО перевищує інтервал."
    )
    public ResponseEntity<List<VehicleResponseDTO>> getRequiringMaintenance() {
        return ResponseEntity.ok(vehicleService.getVehiclesRequiringMaintenance());
    }

    @PostMapping("/{id}/maintenance")
    @Operation(
            summary = "🛠 Провести технічне обслуговування",
            description = "Фіксує факт ТО: оновлює дату, скидає лічильник пробігу після ТО, ставить статус OPERATIONAL."
    )
    public ResponseEntity<Void> performMaintenance(@PathVariable Long id) {
        vehicleService.performMaintenance(id);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}")
    @Operation(summary = "Оновити дані", description = "Часткове оновлення (пробіг, статус, водій).")
    public ResponseEntity<VehicleResponseDTO> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody VehicleUpdateDTO dto) { // <-- Тут мала б бути група OnUpdate, якщо ви створили DTO для Update з групами
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }
}