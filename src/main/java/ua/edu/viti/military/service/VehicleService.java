package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCreateDTO;
import ua.edu.viti.military.dto.request.VehicleUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.entity.Vehicle;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.entity.VehicleStatus;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.mapper.VehicleMapper; // <--- Mapper
import ua.edu.viti.military.repository.DriverRepository;
import ua.edu.viti.military.repository.VehicleCategoryRepository;
import ua.edu.viti.military.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository categoryRepository;
    private final DriverRepository driverRepository; // Додали репозиторій водіїв
    private final VehicleMapper vehicleMapper;       // <--- Inject

    @Transactional
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
        if (vehicleRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new DuplicateResourceException("Машина з номером " + dto.getRegistrationNumber() + " вже існує");
        }

        // 1. Створюємо "заготовку" машини з DTO
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        // 2. Вручну знаходимо та встановлюємо зв'язки (бо в DTO прийшли тільки ID)
        VehicleCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категорію не знайдено"));
        vehicle.setCategory(category);

        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));
            vehicle.setDriver(driver);
        }

        // 3. Встановлюємо статус за замовчуванням
        vehicle.setStatus(VehicleStatus.OPERATIONAL);

        return vehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    public List<VehicleResponseDTO> getAll(VehicleStatus status) {
        List<Vehicle> vehicles;

        // Якщо статус передали - фільтруємо, якщо ні - повертаємо всі
        if (status != null) {
            vehicles = vehicleRepository.findByStatus(status);
        } else {
            vehicles = vehicleRepository.findAll();
        }

        return vehicleMapper.toDTOList(vehicles);
    }

    public VehicleResponseDTO getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));
        return vehicleMapper.toDTO(vehicle);
    }
    public List<VehicleResponseDTO> getVehiclesRequiringMaintenance() {
        // Викликаємо кастомний запит з репозиторію
        List<Vehicle> vehicles = vehicleRepository.findVehiclesRequiringMaintenance();
        // Перетворюємо в DTO через MapStruct
        return vehicleMapper.toDTOList(vehicles);
    }

    @Transactional
    public VehicleResponseDTO update(Long id, VehicleUpdateDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

        // 1. Оновлюємо прості поля через MapStruct
        vehicleMapper.updateEntityFromDTO(dto, vehicle);

        // 2. Оновлюємо зв'язки вручну, якщо вони прийшли в DTO
        if (dto.getCategoryId() != null) {
            VehicleCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Категорію не знайдено"));
            vehicle.setCategory(category);
        }

        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));
            vehicle.setDriver(driver);
        }

        return vehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Машину не знайдено");
        }
        vehicleRepository.deleteById(id);
    }

    @Transactional
    public VehicleResponseDTO performMaintenance(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машину не знайдено"));

        // Фіксуємо факт проведення ТО сьогодні
        vehicle.setLastMaintenanceDate(java.time.LocalDate.now());

        // Записуємо, що ТО зроблено на поточному пробігу
        vehicle.setLastMaintenanceMileage(vehicle.getMileage());

        // Якщо машина була в ремонті або несправна - ставимо статус "Готова"
        vehicle.setStatus(VehicleStatus.OPERATIONAL);

        return vehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }
}