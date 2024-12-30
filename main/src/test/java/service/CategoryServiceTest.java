package service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.MainApplication;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.service.category.CategoryService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        classes = MainApplication.class,
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    Category category;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        category = categoryRepository.save(new Category("category"));
    }

    @Test
    void createTest() {
        CategoryDto created = categoryService.createCategory(new NewCategoryDto("newCategory"));

        assertThat(created.getName(), equalTo("newCategory"));
    }

    @Test
    void getTest() {
        CategoryDto get = categoryService.getCategory(category.getId());

        assertThat(get.getName(), equalTo("category"));
    }

    @Test
    void getCategoriesTest() {
        List<CategoryDto> categories = categoryService.getCategories(0, 10);
        CategoryDto first = categories.getFirst();

        assertThat(categories.size(), equalTo(1));
        assertThat(categories.get(0).getName(), equalTo("category"));
    }

    @Test
    void updateTest() {
        CategoryDto updatedCategory = categoryService.updateCategory(category.getId(), new NewCategoryDto("updatedCategory"));

        assertThat(updatedCategory.getName(), equalTo(categoryRepository.findById(category.getId()).get().getName()));
    }

    @Test
    void deleteTest() {
        categoryService.deleteCategory(category.getId());
        List<Category> all = categoryRepository.findAll();

        assertThat(all.size(), equalTo(0));
    }

}
