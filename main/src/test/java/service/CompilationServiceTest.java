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
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Location;
import ru.practicum.main.repository.*;
import ru.practicum.main.service.category.CategoryService;
import ru.practicum.main.service.compilation.CompilationService;
import ru.practicum.main.service.event.EventService;
import ru.practicum.main.service.request.RequestService;
import ru.practicum.main.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@Transactional
@SpringBootTest(
        classes = MainApplication.class,
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {

    private final RequestService requestService;
    private final EventRequestRepository eventRequestRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final CompilationService compilationService;
    private final CompilationRepository compilationRepository;

    UserDto creator;
    CategoryDto category;
    UserDto requestor;
    EventFullDto event;
    CompilationDto compilation;
    LocalDateTime eventDate = LocalDateTime.now().plusDays(25);
    String stringEventDate = eventDate.format(TIME_FORMATTER);


    @BeforeEach
    void setUp() {
        eventRequestRepository.deleteAll();
        userRepository.deleteAll();
        eventRepository.deleteAll();
        categoryRepository.deleteAll();
        compilationRepository.deleteAll();

        creator = userService.createUser(new NewUserRequest("creator@example.com", "creator"));
        category = categoryService.createCategory(new NewCategoryDto("category"));
        requestor = userService.createUser(new NewUserRequest("requestor@example.com", "requestor"));
        event = eventService.createEvent(creator.getId(), new NewEventDto("base",
                category.getId(), "base", stringEventDate, new Location(10.101010, 10.101010), false,
                10L, true, "base"));
        compilation = compilationService.createCompilation(new NewCompilationDto(Set.of(event.getId()), false, "compilation"));

    }

    @Test
    void createTest() {
        CompilationDto newComp = compilationService.createCompilation(new NewCompilationDto(Set.of(event.getId()), false, "compilation"));

        assertThat(newComp.getEvents().size(), equalTo(1));
        assertThat(newComp.getPinned(), equalTo(false));
        assertThat(newComp.getTitle(), equalTo("compilation"));
    }

    @Test
    void updateTest() {
        CompilationDto compilationDto = compilationService.updateCompilation(compilation.getId(), new UpdateCompilationDto(new HashSet<>(), true, "compilationNew"));

        assertThat(compilationDto.getEvents().size(), equalTo(1));
        assertThat(compilationDto.getPinned(), equalTo(true));
        assertThat(compilationDto.getTitle(), equalTo("compilationNew"));
    }

    @Test
    void deleteTest() {
        compilationService.delete(compilation.getId());

        List<Compilation> all = compilationRepository.findAll();

        assertThat(all.size(), equalTo(0));
    }


}
