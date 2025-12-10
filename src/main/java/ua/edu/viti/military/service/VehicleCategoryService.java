package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.request.VehicleCategoryUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.VehicleCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleCategoryService {

    private final VehicleCategoryRepository categoryRepository;

    @Transactional
    public VehicleCategoryResponseDTO create(VehicleCategoryCreateDTO dto) {
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Категорія з кодом " + dto.getCode() + " вже існує");
        }
        if (categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Категорія з назвою " + dto.getName() + " вже існує");
        }

        VehicleCategory category = new VehicleCategory();
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setDescription(dto.getDescription());
        category.setRequiredLicense(dto.getRequiredLicense());
        category.setMaxLoadCapacity(dto.getMaxLoadCapacity());

        return toDTO(categoryRepository.save(category));
    }

    public VehicleCategoryResponseDTO getById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію з ID " + id + " не знайдено"));
    }

    public List<VehicleCategoryResponseDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleCategoryResponseDTO update(Long id, VehicleCategoryUpdateDTO dto) {
        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію не знайдено"));

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getRequiredLicense() != null) category.setRequiredLicense(dto.getRequiredLicense());
        if (dto.getMaxLoadCapacity() != null) category.setMaxLoadCapacity(dto.getMaxLoadCapacity());

        return toDTO(categoryRepository.save(category));
    }
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категорію з ID " + id + " не знайдено");
        }
        categoryRepository.deleteById(id);
    }
    private VehicleCategoryResponseDTO toDTO(VehicleCategory entity) {
        return new VehicleCategoryResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getRequiredLicense(),
                entity.getMaxLoadCapacity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}