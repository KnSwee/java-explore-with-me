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
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.CreateCommentDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Location;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.category.CategoryService;
import ru.practicum.main.service.comment.CommentService;
import ru.practicum.main.service.event.EventService;
import ru.practicum.main.service.event.mapper.EventDtoMapper;
import ru.practicum.main.service.user.UserService;
import ru.practicum.main.service.user.mapper.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

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
public class CommentServiceTest {


    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    UserDto creator;
    CategoryDto category;
    UserDto commentator;
    EventFullDto event;
    Comment comment;
    LocalDateTime eventDate = LocalDateTime.now().plusDays(25);
    String stringEventDate = eventDate.format(TIME_FORMATTER);


    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        eventRepository.deleteAll();
        categoryRepository.deleteAll();

        creator = userService.createUser(new NewUserRequest("creator@example.com", "creator"));
        category = categoryService.createCategory(new NewCategoryDto("category"));
        commentator = userService.createUser(new NewUserRequest("requestor@example.com", "commentator"));
        event = eventService.createEvent(creator.getId(), new NewEventDto("base",
                category.getId(), "base", stringEventDate, new Location(10.101010, 10.101010), false,
                10L, true, "base"));
        comment = commentRepository.save(new Comment(1L, "comment", LocalDateTime.now(),
                UserDtoMapper.toUser(commentator), EventDtoMapper.toEvent(event), null));
    }

    @Test
    void createTest() {
        CommentDto comment = commentService.create(event.getId(), new CreateCommentDto("commentNew"), commentator.getId());

        assertThat(comment.getAuthor().getName(), equalTo(commentator.getName()));
        assertThat(comment.getText(), equalTo("commentNew"));
    }

    @Test
    void updateTest() {
        CommentDto newText = commentService.update(event.getId(), comment.getId(), new CreateCommentDto("newText"), commentator.getId());

        assertThat(newText.getText(), equalTo("newText"));
        assertThat(newText.getAuthor().getName(), equalTo(commentator.getName()));
    }

    @Test
    void deleteTest() {
        commentService.delete(commentator.getId(), comment.getId());

        List<Comment> all = commentRepository.findAll();

        assertThat(all.size(), equalTo(0));
    }

}
