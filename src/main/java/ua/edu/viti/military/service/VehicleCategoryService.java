package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.request.VehicleCategoryCreateDTO;
import ua.edu.viti.military.dto.request.VehicleCategoryUpdateDTO;
import ua.edu.viti.military.dto.response.VehicleCategoryResponseDTO;
import ua.edu.viti.military.entity.VehicleCategory;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.mapper.VehicleCategoryMapper;
import ua.edu.viti.military.repository.VehicleCategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleCategoryService {

    private final VehicleCategoryRepository categoryRepository;
    private final VehicleCategoryMapper categoryMapper;

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public VehicleCategoryResponseDTO create(VehicleCategoryCreateDTO dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("Категорія з назвою '" + dto.getName() + "' вже існує");
        }

        // MapStruct: DTO -> Entity
        VehicleCategory category = categoryMapper.toEntity(dto);

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Cacheable(value = "categories")
    public List<VehicleCategoryResponseDTO> getAll() {
        // MapStruct: List<Entity> -> List<DTO>
        return categoryMapper.toDTOList(categoryRepository.findAll());
    }

    @Cacheable(value = "categories", key = "#id")
    public VehicleCategoryResponseDTO getById(Long id) {
        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію не знайдено"));
        return categoryMapper.toDTO(category);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public VehicleCategoryResponseDTO update(Long id, VehicleCategoryUpdateDTO dto) {
        VehicleCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категорію не знайдено"));

        // Перевірка на унікальність імені (якщо воно змінилося)
        if (dto.getName() != null &&
                !category.getName().equals(dto.getName()) &&
                categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("Категорія з назвою '" + dto.getName() + "' вже існує");
        }

        // Тепер маппер прийме правильний тип
        categoryMapper.updateEntityFromDTO(dto, category);

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Категорію не знайдено");
        }
        categoryRepository.deleteById(id);
    }
}