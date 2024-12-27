package ru.practicum.main.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.exception.DataConflictException;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.category.mapper.CategoryDtoMapper;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;


    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DataConflictException("Категория с именем " + categoryDto.getName() + " уже существует");
        }
        Category category = categoryRepository.save(new Category(categoryDto.getName()));
        return CategoryDtoMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ElementNotFoundException("Категории с id " + catId + "не существует"));
        if (eventRepository.existsByCategory(category)) {
            throw new DataConflictException("Категория имеет связанные события. Удаление невозможно");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ElementNotFoundException("Категории с id " + catId + "не существует"));
        if (!category.getName().equals(categoryDto.getName())) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                throw new DataConflictException("Категория с именем " + categoryDto.getName() + " уже существует");
            }
        }
        Category updatedCategory = categoryRepository.save(new Category(catId, categoryDto.getName()));
        return CategoryDtoMapper.toCategoryDto(updatedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return CategoryDtoMapper.toCategoryDto(categories);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new ElementNotFoundException("Категории с id " + catId + "не существует"));
        return CategoryDtoMapper.toCategoryDto(category);
    }
}
