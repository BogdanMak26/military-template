package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "2. Водії", description = "Управління водіями та їх документами")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Додати водія")
    public ResponseEntity<DriverResponseDTO> create(
            @Valid @RequestBody DriverCreateDTO dto) {
        log.info("REST request to create driver: {}", dto);
        DriverResponseDTO created = driverService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати водія за ID")
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Отримати всіх активних водіїв")
    public ResponseEntity<List<DriverResponseDTO>> getAll() {
        return ResponseEntity.ok(driverService.getAllActive());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити дані водія")
    public ResponseEntity<DriverResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DriverUpdateDTO dto) {
        return ResponseEntity.ok(driverService.update(id, dto));
    }
}