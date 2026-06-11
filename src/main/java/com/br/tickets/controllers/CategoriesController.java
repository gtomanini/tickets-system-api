package com.br.tickets.controllers;

import com.br.tickets.enums.AgeRating;
import com.br.tickets.models.dto.AgeRatingDTO;
import com.br.tickets.models.dto.CategoryListDTO;
import com.br.tickets.models.dto.CreateCategoryDTO;
import com.br.tickets.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Categories & Age Ratings")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CategoriesController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryListDTO>> list() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryListDTO> create(@RequestBody @Valid CreateCategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryListDTO> update(@PathVariable Long id, @RequestBody @Valid CreateCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/age-ratings")
    public ResponseEntity<List<AgeRatingDTO>> listAgeRatings() {
        List<AgeRatingDTO> ratings = Arrays.stream(AgeRating.values())
                .map(AgeRatingDTO::from)
                .toList();
        return ResponseEntity.ok(ratings);
    }
}
