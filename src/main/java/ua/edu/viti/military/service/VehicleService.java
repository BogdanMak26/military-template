package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.*;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.DriverRepository;
import ua.edu.viti.military.repository.VehicleCategoryRepository;
import ua.edu.viti.military.repository.VehicleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository categoryRepository;
    private final DriverRepository driverRepository;
    private final DriverService driverService; // Використовуємо для маппінгу водія

    // === CREATE ===
    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        log.info("Creating vehicle with reg number: {}", dto.getRegistrationNumber());

        // 1. Перевірка унікальності номера
        if (vehicleRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException("Транспорт з номером " + dto.getRegistrationNumber() + " вже існує");
        }

        // 2. Пошук категорії
        VehicleCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + dto.getCategoryId() + " не знайдено"));

        // 3. Пошук водія (опційно)
        Driver driver = null;
        if (dto.getDriverId() != null) {
            driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + dto.getDriverId() + " не знайдено"));
        }

        // 4. Створення Entity
        Vehicle vehicle = new Vehicle();
        vehicle.setModel(dto.getModel());
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setCategory(category);
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setChassisNumber(dto.getChassisNumber());
        vehicle.setManufactureYear(dto.getManufactureYear());
        vehicle.setMileage(dto.getMileage());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setFuelConsumption(dto.getFuelConsumption());
        vehicle.setMaintenanceIntervalKm(dto.getMaintenanceIntervalKm());
        vehicle.setLastMaintenanceDate(dto.getLastMaintenanceDate());
        vehicle.setLastMaintenanceMileage(dto.getLastMaintenanceMileage());
        vehicle.setDriver(driver);
        vehicle.setStatus(dto.getStatus());

        return toDTO(vehicleRepository.save(vehicle));
    }

    // === READ ===
    public VehicleResponseDTO getById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Транспорт не знайдено"));
    }

    public List<VehicleResponseDTO> getAll(VehicleStatus status) {
        List<Vehicle> vehicles;
        if (status != null) {
            // Використовуємо оптимізований метод з JOIN FETCH (щоб не було N+1)
            vehicles = vehicleRepository.findByStatusWithDetails(status);
        } else {
            vehicles = vehicleRepository.findAll();
        }
        return vehicles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Специфічний метод: знайти машини, яким треба ТО
    public List<VehicleResponseDTO> getVehiclesRequiringMaintenance() {
        return vehicleRepository.findVehiclesRequiringMaintenance().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === UPDATE ===
    @Transactional
    public VehicleResponseDTO update(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транспорт не знайдено"));

        if (dto.getMileage() != null) vehicle.setMileage(dto.getMileage());
        if (dto.getStatus() != null) vehicle.setStatus(dto.getStatus());
        if (dto.getFuelConsumption() != null) vehicle.setFuelConsumption(dto.getFuelConsumption());

        // Оновлення ТО
        if (dto.getLastMaintenanceDate() != null) vehicle.setLastMaintenanceDate(dto.getLastMaintenanceDate());
        if (dto.getLastMaintenanceMileage() != null) vehicle.setLastMaintenanceMileage(dto.getLastMaintenanceMileage());

        // Логіка зміни водія
        if (dto.getDriverId() != null) {
            Driver newDriver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));
            vehicle.setDriver(newDriver);
        } else {
            // Якщо передали null, можна, наприклад, залишити старого або видалити водія
            // Тут реалізуємо: якщо driverId не передано - не змінюємо.
            // Щоб зняти водія, треба передати спеціальний прапор або -1 (залежить від вимог),
            // але поки залишимо просту логіку.
        }

        return toDTO(vehicleRepository.save(vehicle));
    }

    // Метод для проведення ТО (бізнес-дія)
    @Transactional
    public void performMaintenance(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Транспорт не знайдено"));

        vehicle.setLastMaintenanceDate(LocalDate.now());
        vehicle.setLastMaintenanceMileage(vehicle.getMileage());
        vehicle.setStatus(VehicleStatus.OPERATIONAL);

        vehicleRepository.save(vehicle);
        log.info("Maintenance performed for vehicle ID: {}", vehicleId);
    }

    // === MAPPING ===
    private VehicleResponseDTO toDTO(Vehicle entity) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(entity.getId());
        dto.setModel(entity.getModel());
        dto.setRegistrationNumber(entity.getRegistrationNumber());

        dto.setCategory(new VehicleCategoryResponseDTO(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getCode(),
                entity.getCategory().getDescription(),
                entity.getCategory().getRequiredLicense(),
                entity.getCategory().getMaxLoadCapacity(),
                entity.getCategory().getCreatedAt(),
                entity.getCategory().getUpdatedAt()
        ));

        dto.setEngineNumber(entity.getEngineNumber());
        dto.setChassisNumber(entity.getChassisNumber());
        dto.setManufactureYear(entity.getManufactureYear());
        dto.setMileage(entity.getMileage());
        dto.setFuelType(entity.getFuelType());
        dto.setFuelConsumption(entity.getFuelConsumption());
        dto.setMaintenanceIntervalKm(entity.getMaintenanceIntervalKm());
        dto.setLastMaintenanceDate(entity.getLastMaintenanceDate());
        dto.setLastMaintenanceMileage(entity.getLastMaintenanceMileage());

        // Маппінг водія через сервіс водіїв або вручну
        if (entity.getDriver() != null) {
            dto.setDriver(driverService.toDTO(entity.getDriver()));
        }

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}