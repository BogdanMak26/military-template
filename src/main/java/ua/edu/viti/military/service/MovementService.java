package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.MovementRequestDTO;
import ua.edu.viti.military.dto.response.MovementResponseDTO;
import ua.edu.viti.military.entity.*;
import ua.edu.viti.military.event.MaintenanceCompletedEvent;
import ua.edu.viti.military.event.MaintenanceStartedEvent;
import ua.edu.viti.military.exception.BusinessLogicException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.DriverRepository;
import ua.edu.viti.military.repository.VehicleMovementRepository;
import ua.edu.viti.military.repository.VehicleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleMovementRepository movementRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MetricsService metricsService;

    // --- ОПЕРАЦІЯ 1: Призначення водія ---
    @Transactional
    public MovementResponseDTO assignDriver(MovementRequestDTO dto) {
        Vehicle vehicle = getVehicle(dto.getVehicleId());

        if (dto.getDriverId() == null) {
            throw new BusinessLogicException("Для призначення необхідний ID водія");
        }
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));

        if (!driver.getIsActive()) {
            throw new BusinessLogicException("Не можна призначити звільненого водія");
        }

        // Змінюємо стан машини
        vehicle.setDriver(driver);
        vehicleRepository.save(vehicle);
        metricsService.incrementDriverAssigned();
        // Записуємо в журнал
        return createLog(vehicle, driver, MovementType.ASSIGN_DRIVER, dto.getNotes());
    }

    // --- ОПЕРАЦІЯ 2: Відправка на ремонт ---
    @Transactional
    public MovementResponseDTO startMaintenance(MovementRequestDTO dto) {
        Vehicle vehicle = getVehicle(dto.getVehicleId());

        if (vehicle.getStatus() == VehicleStatus.IN_MAINTENANCE) {
            throw new BusinessLogicException("Машина вже в ремонті");
        }

        // Змінюємо статус
        vehicle.setStatus(VehicleStatus.IN_MAINTENANCE);
        vehicleRepository.save(vehicle);
        eventPublisher.publishEvent(new MaintenanceStartedEvent(this, vehicle, dto.getNotes()));

        log.info("Подія MaintenanceStartedEvent опублікована для машини {}", vehicle.getRegistrationNumber());
        metricsService.incrementMaintenanceStarted();

        return createLog(vehicle, vehicle.getDriver(), MovementType.START_MAINTENANCE, dto.getNotes());
    }


    // --- ОПЕРАЦІЯ 3: Завершення ремонту ---
    @Transactional
    public MovementResponseDTO completeMaintenance(MovementRequestDTO dto) {
        Vehicle vehicle = getVehicle(dto.getVehicleId());

        if (vehicle.getStatus() != VehicleStatus.IN_MAINTENANCE) {
            throw new BusinessLogicException("Машина не була в ремонті");
        }

        // Оновлюємо статус і дані про ТО
        vehicle.setStatus(VehicleStatus.OPERATIONAL);
        vehicle.setLastMaintenanceDate(LocalDate.now());
        vehicle.setLastMaintenanceMileage(vehicle.getMileage()); // ТО зроблено на поточному пробігу
        vehicleRepository.save(vehicle);
        metricsService.incrementMaintenanceCompleted();

        eventPublisher.publishEvent(new MaintenanceCompletedEvent(this, vehicle));
        metricsService.incrementMaintenanceCompleted();

        return createLog(vehicle, vehicle.getDriver(), MovementType.COMPLETE_MAINTENANCE, dto.getNotes());
    }


    private VehicleMovement createLogEntry(Vehicle vehicle, Driver driver, MovementType type, String notes) {
        VehicleMovement log = new VehicleMovement();
        log.setVehicle(vehicle);
        log.setDriver(driver);
        log.setType(type);
        log.setMileageAtEvent(vehicle.getMileage());
        log.setNotes(notes);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.setPerformedBy(currentUsername);

        return movementRepository.save(log);
    }

    private MovementResponseDTO createLog(Vehicle vehicle, Driver driver, MovementType type, String notes) {
        VehicleMovement saved = createLogEntry(vehicle, driver, type, notes);
        return mapToDTO(saved);
    }

    private Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транспорт не знайдено"));
    }

    public List<MovementResponseDTO> getHistory(Long vehicleId) {
        return movementRepository.findByVehicleIdOrderByPerformedAtDesc(vehicleId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private MovementResponseDTO mapToDTO(VehicleMovement entity) {
        String driverInfo = entity.getDriver() != null
                ? entity.getDriver().getRank() + " " + entity.getDriver().getLastName()
                : "Без водія";

        return MovementResponseDTO.builder()
                .id(entity.getId())
                .vehicleNumber(entity.getVehicle().getRegistrationNumber())
                .driverName(driverInfo)
                .type(entity.getType())
                .mileage(entity.getMileageAtEvent())
                .notes(entity.getNotes())
                .performedAt(entity.getPerformedAt())
                .build();
    }
}