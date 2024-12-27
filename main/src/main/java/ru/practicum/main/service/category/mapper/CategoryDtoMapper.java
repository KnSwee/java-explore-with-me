package ru.practicum.main.service.category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.model.Category;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDtoMapper {

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    public static List<CategoryDto> toCategoryDto(List<Category> categories) {
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            categoryDtos.add(toCategoryDto(category));
        }
        return categoryDtos;
    }

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

}
