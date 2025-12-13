package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.MovementRequestDTO;
import ua.edu.viti.military.dto.response.MovementResponseDTO;
import ua.edu.viti.military.service.MovementService;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "4. Журнал операцій", description = "Управління станом техніки та закріпленням водіїв")
public class MovementController {

    private final MovementService movementService;

    @PostMapping("/assign-driver")
    @Operation(summary = "Призначити водія на машину", description = "Закріплює водія за транспортним засобом та створює запис у журналі.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Водія успішно призначено"),
            @ApiResponse(responseCode = "404", description = "Машину або водія не знайдено"),
            @ApiResponse(responseCode = "400", description = "Водій звільнений або неактивний")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MovementResponseDTO> assignDriver(@Valid @RequestBody MovementRequestDTO dto) {
        log.info("Request to assign driver {} to vehicle {}", dto.getDriverId(), dto.getVehicleId());
        return ResponseEntity.ok(movementService.assignDriver(dto));
    }

    @PostMapping("/start-maintenance")
    @Operation(summary = "Відправити машину на ремонт", description = "Змінює статус машини на IN_MAINTENANCE та сповіщає ремонтний підрозділ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Машину відправлено на ремонт"),
            @ApiResponse(responseCode = "400", description = "Машина вже в ремонті")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MovementResponseDTO> startMaintenance(@Valid @RequestBody MovementRequestDTO dto) {
        log.info("Request to start maintenance for vehicle {}", dto.getVehicleId());
        return ResponseEntity.ok(movementService.startMaintenance(dto));
    }

    @PostMapping("/complete-maintenance")
    @Operation(summary = "Повернути машину з ремонту", description = "Змінює статус на OPERATIONAL, оновлює дату ТО та пробіг.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ремонт завершено"),
            @ApiResponse(responseCode = "400", description = "Машина не була в ремонті")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MovementResponseDTO> completeMaintenance(@Valid @RequestBody MovementRequestDTO dto) {
        return ResponseEntity.ok(movementService.completeMaintenance(dto));
    }

    @GetMapping("/history/{vehicleId}")
    @Operation(summary = "Отримати історію машини", description = "Показує всі події: призначення водіїв, ремонти, списання.")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public ResponseEntity<List<MovementResponseDTO>> getHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(movementService.getHistory(vehicleId));
    }
}