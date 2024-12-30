package service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.MainApplication;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.enums.State;
import ru.practicum.main.enums.StateActionAdmin;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Location;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.category.CategoryService;
import ru.practicum.main.service.category.mapper.CategoryDtoMapper;
import ru.practicum.main.service.event.EventService;
import ru.practicum.main.service.event.mapper.EventDtoMapper;
import ru.practicum.main.service.request.RequestService;
import ru.practicum.main.service.user.UserService;
import ru.practicum.main.service.user.mapper.UserDtoMapper;
import ru.practicum.stats.client.StatClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@Transactional
@SpringBootTest(
        classes = MainApplication.class,
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final RequestService requestService;
    @MockBean
    private final StatClient statClient;
    UserDto user;
    CategoryDto category;
    EventFullDto baseEvent;
    LocalDateTime eventDate = LocalDateTime.now().plusDays(25);
    String stringEventDate = eventDate.format(TIME_FORMATTER);


    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        category = categoryService.createCategory(new NewCategoryDto("category"));
        user = userService.createUser(new NewUserRequest("example@example.com", "userName"));
        baseEvent = EventDtoMapper.toEventDto(eventRepository.save(new Event(1L, "base",
                CategoryDtoMapper.toCategory(category), 5L, LocalDateTime.now(), "base",
                eventDate, UserDtoMapper.toUser(user), 10.101010, 10.101010, false,
                10L, null, true, State.PENDING, "base")));
    }

    @Test
    void createTest() {
        EventFullDto event = eventService.createEvent(user.getId(), new NewEventDto("annotation",
                category.getId(), "description", stringEventDate, new Location(10.101010, 10.101010),
                true, 10L, false, "title"));
        System.out.println(eventRepository.findAll());
        Event first = eventRepository.findById(event.getId()).orElse(null);

        assertThat(event.getId(), is(equalTo(first.getId())));
        assertThat(event.getEventDate(), is(equalTo(first.getEventDate().format(TIME_FORMATTER))));
        assertThat(event.getDescription(), is(equalTo(first.getDescription())));
        assertThat(event.getCategory().getId(), is(equalTo(category.getId())));
        assertThat(event.getCategory().getName(), is(equalTo(category.getName())));
    }

    @Test
    void updateAdminTest() {
        UpdateEventAdminRequest upd = new UpdateEventAdminRequest();
        upd.setStateAction(StateActionAdmin.PUBLISH_EVENT.toString());

        EventFullDto eventFullDto = eventService.patchAdminEvent(baseEvent.getId(), upd);

        assertThat(eventFullDto.getState(), is(equalTo(State.PUBLISHED.toString())));
    }

    @Test
    void updateTest() {
        UpdateEventUserRequest upd = new UpdateEventUserRequest();
        upd.setAnnotation("newAnnotation");
        upd.setDescription("newDescription");
        EventFullDto eventFullDto = eventService.patchPrivateEvent(user.getId(), baseEvent.getId(), upd);

        assertThat(eventFullDto.getAnnotation(), is(equalTo("newAnnotation")));
        assertThat(eventFullDto.getDescription(), is(equalTo("newDescription")));
    }

    @Test
    void getByCreatorTest() {
        EventFullDto eventByCreator = eventService.getEventByCreator(user.getId(), baseEvent.getId());

        assertThat(eventByCreator.getState(), is(equalTo(baseEvent.getState())));
        assertThat(eventByCreator.getEventDate(), is(equalTo(baseEvent.getEventDate())));
        assertThat(eventByCreator.getDescription(), is(equalTo(baseEvent.getDescription())));
    }

    @Test
    void getPrivateEventsTest() {
        List<EventShortDto> privateEvents = eventService.getPrivateEvents(user.getId(), 0, 10);
        EventShortDto first = privateEvents.getFirst();
        assertThat(privateEvents.size(), is(equalTo(1)));
        assertThat(first.getId(), is(equalTo(baseEvent.getId())));
        assertThat(first.getEventDate(), is(equalTo(baseEvent.getEventDate())));
    }

    @Test
    void getPublicEventTest() {
        baseEvent.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(baseEvent));
        EventFullDto publicEvent = eventService.getPublicEvent(baseEvent.getId(), new MockHttpServletRequest());

        assertThat(publicEvent.getEventDate(), is(equalTo(baseEvent.getEventDate())));
        assertThat(publicEvent.getDescription(), is(equalTo(baseEvent.getDescription())));
        assertThat(publicEvent.getCategory().getId(), is(equalTo(category.getId())));
    }

    @Test
    void getEventRequestTest() {
        baseEvent.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(baseEvent));
        UserDto requestor = userService.createUser(new NewUserRequest("example3@example.com", "userName"));
        ParticipationRequestDto request = requestService.createRequest(requestor.getId(), baseEvent.getId());

        List<ParticipationRequestDto> eventRequestsByUser = eventService.getEventRequestsByUser(user.getId(), baseEvent.getId());
        ParticipationRequestDto first = eventRequestsByUser.getFirst();

        assertThat(eventRequestsByUser.size(), is(equalTo(1)));
        assertThat(first.getId(), is(equalTo(request.getId())));
        assertThat(first.getStatus(), is(equalTo(request.getStatus())));
    }

    @Test
    void searchEventsAdminTest() {
        List<EventFullDto> eventFullDtos = eventService.searchEventsAdmin(List.of(user.getId()), null,
                null, "2011-01-01 00:00:00", "2099-01-01 00:00:00", 0, 10);
        EventFullDto first = eventFullDtos.getFirst();
        assertThat(eventFullDtos.size(), is(equalTo(1)));
        assertThat(first.getId(), is(equalTo(baseEvent.getId())));
        assertThat(first.getEventDate(), is(equalTo(baseEvent.getEventDate())));

    }

    @Test
    void searchPrivateEventsTest() {
        baseEvent.setState(State.PUBLISHED.toString());
        eventRepository.save(EventDtoMapper.toEvent(baseEvent));
        List<EventShortDto> events = eventService.searchPublicEvents("base", List.of(category.getId()), null, null,
                null, true, null, 0, 10, new MockHttpServletRequest());
        EventShortDto first = events.getFirst();
        assertThat(events.size(), is(equalTo(1)));
        assertThat(first.getId(), is(equalTo(baseEvent.getId())));
        assertThat(first.getEventDate(), is(equalTo(baseEvent.getEventDate())));
    }

}
