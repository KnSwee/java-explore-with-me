package ru.practicum.main.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.service.category.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    private void deleteCategory(@PathVariable(name = "catId") Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    private CategoryDto updateCategory(@PathVariable(name = "catId") Long catId,
                                       @Valid @RequestBody NewCategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

}
