package ru.practicum.main.service.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto);

    List<CategoryDto> getCategories(@PositiveOrZero Integer from, @Positive Integer size);

    CategoryDto getCategory(Long catId);
}
