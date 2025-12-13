package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.DriverCreateDTO;
import ua.edu.viti.military.dto.request.DriverUpdateDTO;
import ua.edu.viti.military.dto.response.DriverResponseDTO;
import ua.edu.viti.military.entity.Driver;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.mapper.DriverMapper; // <--- Mapper
import ua.edu.viti.military.repository.DriverRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper; // <--- Inject

    @Transactional
    public DriverResponseDTO create(DriverCreateDTO dto) {
        if (driverRepository.findByMilitaryId(dto.getMilitaryId()).isPresent()) {
            throw new DuplicateResourceException("Водій з військовим квитком " + dto.getMilitaryId() + " вже існує");
        }
        if (driverRepository.findByLicenseNumber(dto.getLicenseNumber()).isPresent()) {
            throw new DuplicateResourceException("Водій з правами " + dto.getLicenseNumber() + " вже існує");
        }

        // MapStruct: DTO -> Entity
        Driver driver = driverMapper.toEntity(dto);

        // Встановлюємо дефолтні значення, якщо їх немає в DTO або маппері
        if (driver.getIsActive() == null) {
            driver.setIsActive(true);
        }

        return driverMapper.toDTO(driverRepository.save(driver));
    }

    public DriverResponseDTO getById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toDTO) // Використовуємо метод маппера як посилання
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + id + " не знайдено"));
    }

    public List<DriverResponseDTO> getAllActive() {
        return driverMapper.toDTOList(driverRepository.findByIsActive(true));
    }

    @Transactional
    public DriverResponseDTO update(Long id, DriverUpdateDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));

        // 🔥 МАГІЯ: MapStruct сам перевіряє null і оновлює тільки потрібні поля
        driverMapper.updateEntityFromDTO(dto, driver);

        return driverMapper.toDTO(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Водія з ID " + id + " не знайдено");
        }
        driverRepository.deleteById(id);
    }
}