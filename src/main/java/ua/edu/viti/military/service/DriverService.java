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
import ua.edu.viti.military.repository.DriverRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;

    @Transactional
    public DriverResponseDTO create(DriverCreateDTO dto) {
        if (driverRepository.findByMilitaryId(dto.getMilitaryId()).isPresent()) {
            throw new DuplicateResourceException("Водій з військовим квитком " + dto.getMilitaryId() + " вже існує");
        }
        if (driverRepository.findByLicenseNumber(dto.getLicenseNumber()).isPresent()) {
            throw new DuplicateResourceException("Водій з правами " + dto.getLicenseNumber() + " вже існує");
        }

        Driver driver = new Driver();
        driver.setMilitaryId(dto.getMilitaryId());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setMiddleName(dto.getMiddleName());
        driver.setRank(dto.getRank());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setLicenseCategories(dto.getLicenseCategories());
        driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setIsActive(dto.getIsActive());

        return toDTO(driverRepository.save(driver));
    }

    public DriverResponseDTO getById(Long id) {
        return driverRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Водія з ID " + id + " не знайдено"));
    }

    public List<DriverResponseDTO> getAllActive() {
        return driverRepository.findByIsActive(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverResponseDTO update(Long id, DriverUpdateDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Водія не знайдено"));

        if (dto.getFirstName() != null) driver.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) driver.setLastName(dto.getLastName());
        if (dto.getMiddleName() != null) driver.setMiddleName(dto.getMiddleName());
        if (dto.getRank() != null) driver.setRank(dto.getRank());
        if (dto.getLicenseNumber() != null) driver.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getLicenseCategories() != null) driver.setLicenseCategories(dto.getLicenseCategories());
        if (dto.getLicenseExpiryDate() != null) driver.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        if (dto.getPhoneNumber() != null) driver.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getIsActive() != null) driver.setIsActive(dto.getIsActive());

        return toDTO(driverRepository.save(driver));
    }
    @Transactional
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Водія з ID " + id + " не знайдено");
        }
        driverRepository.deleteById(id);
    }

    // --- MAPPING ---
    public DriverResponseDTO toDTO(Driver entity) {
        if (entity == null) return null;
        return new DriverResponseDTO(
                entity.getId(),
                entity.getMilitaryId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getRank(),
                entity.getLicenseNumber(),
                entity.getLicenseCategories(),
                entity.getLicenseExpiryDate(),
                entity.getPhoneNumber(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}