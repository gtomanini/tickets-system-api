package com.br.tickets.services;

import com.br.tickets.models.EventCategory;
import com.br.tickets.models.dto.CategoryListDTO;
import com.br.tickets.models.dto.CreateCategoryDTO;
import com.br.tickets.repositories.EventCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final EventCategoryRepository categoryRepository;

    public List<CategoryListDTO> listAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoryListDTO create(CreateCategoryDTO dto) {
        EventCategory category = EventCategory.builder()
                .name(dto.name())
                .description(dto.description())
                .build();
        return toDTO(categoryRepository.save(category));
    }

    public CategoryListDTO update(Long id, CreateCategoryDTO dto) {
        EventCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setName(dto.name());
        category.setDescription(dto.description());
        return toDTO(categoryRepository.save(category));
    }

    public void delete(Long id) {
        EventCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private CategoryListDTO toDTO(EventCategory c) {
        return new CategoryListDTO(c.getId(), c.getName(), c.getDescription());
    }
}
