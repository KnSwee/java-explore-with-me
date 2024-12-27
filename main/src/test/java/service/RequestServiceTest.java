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
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.State;
import ru.practicum.main.model.Location;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.EventRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.category.CategoryService;
import ru.practicum.main.service.event.EventService;
import ru.practicum.main.service.event.mapper.EventDtoMapper;
import ru.practicum.main.service.request.RequestService;
import ru.practicum.main.service.user.UserService;

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
public class RequestServiceTest {

    private final RequestService requestService;
    private final EventRequestRepository eventRequestRepository;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    UserDto creator;
    CategoryDto category;
    UserDto requestor;
    EventFullDto event;
    LocalDateTime eventDate = LocalDateTime.now().plusDays(25);
    String stringEventDate = eventDate.format(TIME_FORMATTER);


    @BeforeEach
    void setUp() {
        eventRequestRepository.deleteAll();
        userRepository.deleteAll();
        eventRepository.deleteAll();
        categoryRepository.deleteAll();

        creator = userService.createUser(new NewUserRequest("creator@example.com", "creator"));
        category = categoryService.createCategory(new NewCategoryDto("category"));
        requestor = userService.createUser(new NewUserRequest("requestor@example.com", "requestor"));
        event = eventService.createEvent(creator.getId(), new NewEventDto("base",
                category.getId(), "base", stringEventDate, new Location(10.101010, 10.101010), false,
                10L, true, "base"));

    }

    @Test
    void createRequestTest() {
        event.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(event));
        ParticipationRequestDto request = requestService.createRequest(requestor.getId(), event.getId());

        assertThat(request.getRequester(), equalTo(requestor.getId()));
        assertThat(request.getEvent(), equalTo(event.getId()));
        assertThat(request.getStatus(), equalTo(RequestStatus.PENDING.toString()));
    }

    @Test
    void getRequestTest() {
        event.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(event));
        requestService.createRequest(requestor.getId(), event.getId());

        List<ParticipationRequestDto> requests = requestService.getRequests(requestor.getId());
        ParticipationRequestDto first = requests.getFirst();

        assertThat(requests.size(), equalTo(1));
        assertThat(first.getRequester(), equalTo(requestor.getId()));
        assertThat(first.getEvent(), equalTo(event.getId()));
    }

    @Test
    void patchRequestTest() {
        event.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(event));
        ParticipationRequestDto request = requestService.createRequest(requestor.getId(), event.getId());
        System.out.println(eventRequestRepository.findAll());
        requestService.cancelRequest(requestor.getId(), request.getId());

        List<ParticipationRequestDto> requests = requestService.getRequests(requestor.getId());
        ParticipationRequestDto first = requests.getFirst();

        assertThat(requests.size(), equalTo(1));
        assertThat(first.getRequester(), equalTo(requestor.getId()));
        assertThat(first.getStatus(), equalTo(RequestStatus.CANCELED.toString()));
    }


}
