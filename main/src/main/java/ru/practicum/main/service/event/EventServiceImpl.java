package ru.practicum.main.service.event;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.enums.*;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.DataConflictException;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventRequest;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.EventRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.category.CategoryService;
import ru.practicum.main.service.category.mapper.CategoryDtoMapper;
import ru.practicum.main.service.event.mapper.EventDtoMapper;
import ru.practicum.main.service.request.mapper.EventRequestDtoMapper;
import ru.practicum.stats.client.StatClient;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final EventRequestRepository requestRepository;
    private final StatClient statClient;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (LocalDateTime.parse(newEventDto.getEventDate(), TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "дата и время начала события не может быть раньше, чем через два часа от текущего момента");
        }
        Category category = CategoryDtoMapper.toCategory(categoryService.getCategory(newEventDto.getCategory()));
        Event event = EventDtoMapper.toEvent(newEventDto, category);
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователя с id " + userId + " не существует")));
        event.setState(State.PENDING);
        Event newEvent = eventRepository.save(event);
        return EventDtoMapper.toEventDto(newEvent);
    }

    @Override
    public EventFullDto patchPrivateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ElementNotFoundException("События с id " + eventId + "не существует"));
        if (event.getState() == State.PUBLISHED) {
            throw new DataConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getEventDate() != null && !updateEventUserRequest.getEventDate().isBlank()) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventUserRequest.getEventDate(), TIME_FORMATTER);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException(
                        "дата и время начала события не может быть раньше, чем через два часа от текущего момента");
            } else {
                event.setEventDate(eventDate);
            }
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(CategoryDtoMapper.toCategory(categoryService.getCategory(updateEventUserRequest.getCategory())));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLatitude(updateEventUserRequest.getLocation().getLat());
            event.setLongitude(updateEventUserRequest.getLocation().getLon());
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null && !updateEventUserRequest.getStateAction().isBlank()) {
            switch (StateActionUser.valueOf(updateEventUserRequest.getStateAction())) {
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
            }
        }
        EventFullDto newEventDto = EventDtoMapper.toEventDto(eventRepository.save(event));
        Long views = getViews(List.of(newEventDto.getId())).get(newEventDto.getId());
        newEventDto.setViews(views);
        return newEventDto;
    }

    @Override
    public EventFullDto patchAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ElementNotFoundException("События с id " + eventId + "не существует"));
        if (event.getState() != State.PENDING) {
            throw new DataConflictException("Only pending events can be changed");
        }
        if (updateEventAdminRequest.getEventDate() != null && !updateEventAdminRequest.getEventDate().isBlank()) {
            LocalDateTime eventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), TIME_FORMATTER);
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException(
                        "дата и время начала события не может быть раньше, чем через час от текущего момента");
            } else {
                event.setEventDate(eventDate);
            }
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(CategoryDtoMapper.toCategory(categoryService.getCategory(updateEventAdminRequest.getCategory())));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLatitude(updateEventAdminRequest.getLocation().getLat());
            event.setLongitude(updateEventAdminRequest.getLocation().getLon());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null && !updateEventAdminRequest.getStateAction().isBlank()) {
            switch (StateActionAdmin.valueOf(updateEventAdminRequest.getStateAction())) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
            }
        }
        EventFullDto newEvent = EventDtoMapper.toEventDto(eventRepository.save(event));
        Long views = getViews(List.of(newEvent.getId())).get(newEvent.getId());
        newEvent.setViews(views);
        return newEvent;
    }

    @Override
    public EventRequestStatusUpdateResult patchEventRequestsByUser(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate) {

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new ElementNotFoundException("События с id " + eventId + " созданного пользователем с id " + userId + " не найдено"));

        List<Long> requestIds = requestUpdate.getRequestIds();
        List<EventRequest> requests = requestRepository.findByIdIn(requestIds);
        for (EventRequest eventRequest : requests) {
            if (!(eventRequest.getStatus() == RequestStatus.PENDING)) {
                throw new DataConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        }

        Long confirmedRequests = event.getConfirmedRequests();
        Long participantLimit = event.getParticipantLimit();
        if (participantLimit <= confirmedRequests) {
            throw new DataConflictException("Лимит участников для события с id " + eventId + " достигнут");
        }
        UpdateStatus status = UpdateStatus.valueOf(requestUpdate.getStatus());
        if (status == UpdateStatus.CONFIRMED) {
            if (participantLimit == 0 || !event.getRequestModeration()) {
                event.setConfirmedRequests(confirmedRequests + requests.size());
                eventRepository.save(event);
                return confirmAll(requests);
            } else {
                return confirmRequests(event, requests);
            }
        } else {
            return rejectAll(requests);
        }
    }

    @Override
    public EventFullDto getEventByCreator(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new ElementNotFoundException("События с id " + eventId + " созданного пользователем с id " + userId + " не найдено"));
        EventFullDto eventDto = EventDtoMapper.toEventDto(event);
        eventDto.setViews(getViews(List.of(eventDto.getId())).get(eventDto.getId()));
        return eventDto;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByUser(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new ElementNotFoundException("События с id " + eventId + " созданного пользователем с id " + userId + " не найдено"));
        return EventRequestDtoMapper.toDto(requestRepository.findByEventId(eventId));
    }

    @Override
    public List<EventFullDto> searchEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        invalidDateExceptionCheck(rangeStart, rangeEnd);
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Event> filteredEvents = eventRepository.findAll(getSpecificationForEventAdmin(users, states, categories, rangeStart, rangeEnd), page).getContent();
        return EventDtoMapper.toEventDto(filteredEvents);
    }

    @Override
    public List<EventShortDto> searchPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        invalidDateExceptionCheck(rangeStart, rangeEnd);

        statClient.save(new EndpointHitDto(null, "ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(TIME_FORMATTER)));

        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Event> filteredEvents = new ArrayList<>(eventRepository.findAll(getSpecificationForEventPublic(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable), page).getContent());

        if (StringUtils.isBlank(sort)) {
            filteredEvents.sort(Comparator.comparing(Event::getCreatedOn).reversed());
        } else if (SortType.valueOf(sort) == SortType.EVENT_DATE) {
            filteredEvents.sort(Comparator.comparing(Event::getEventDate));
        }
        List<EventShortDto> eventDto = EventDtoMapper.toEventShortDto(filteredEvents);
        List<Long> ids = eventDto.stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> views = getViews(ids);
        eventDto.forEach(dto -> dto.setViews(views.get(dto.getId())));
        if (StringUtils.isNotBlank(sort) && SortType.valueOf(sort) == SortType.VIEWS) {
            eventDto.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return eventDto;
    }

    private void invalidDateExceptionCheck(String rangeStart, String rangeEnd) {
        if (!StringUtils.isBlank(rangeStart) && !StringUtils.isBlank(rangeEnd)) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, TIME_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, TIME_FORMATTER);
            if (end.isBefore(start)) {
                throw new BadRequestException("rangeEnd is before rangeStart");
            }
        }
    }

    @Override
    public EventFullDto getPublicEvent(Long id, HttpServletRequest request) {

        Event publicEvent = eventRepository.findById(id).orElseThrow(
                () -> new ElementNotFoundException("События с id " + id + " не существует"));
        if (!publicEvent.getState().equals(State.PUBLISHED)) {
            throw new ElementNotFoundException("События с id " + id + " не опубликовано");
        }
        statClient.save(new EndpointHitDto(null, "ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now().format(TIME_FORMATTER)));
        Map<Long, Long> views = getViews(List.of(publicEvent.getId()));
        EventFullDto eventDto = EventDtoMapper.toEventDto(publicEvent);
        eventDto.setViews(views.get(publicEvent.getId()));

        return eventDto;
    }

    @Override
    public List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(0, size, Sort.by("id").ascending());
        List<Event> events = eventRepository.findByUser(userId, page);
        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Long> views = getViews(ids);
        List<EventShortDto> eventShortDto = EventDtoMapper.toEventShortDto(events);
        eventShortDto.forEach(dto -> dto.setViews(views.get(dto.getId())));
        return eventShortDto;
    }

    private Map<Long, Long> getViews(List<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();

        try {
            viewStatsDtos = statClient.get(
                    LocalDateTime.of(1970, 1, 1, 1, 1),
                    LocalDateTime.of(2099, 1, 1, 0, 0),
                    uris,
                    true);
        } catch (Exception e) {
            log.error("Ошибка при обращении к сервису статистики", e);
        }

        return viewStatsDtos.stream()
                .collect(Collectors.toMap(dto -> Long.parseLong(dto.getUri().substring(8)),
                        ViewStatsDto::getHits,
                        (a, b) -> a));
    }

    private Specification<Event> getSpecificationForEventAdmin(List<Long> users, List<String> states,
                                                               List<Long> categories, String rangeStart,
                                                               String rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            LocalDateTime start;

            if (Objects.nonNull(users) && !users.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(users));
            }

            if (Objects.nonNull(states) && !states.isEmpty()) {
                List<State> stateEnums = states.stream().map(State::valueOf).toList();
                predicates.add(root.get("state").in(stateEnums));
            }

            if (Objects.nonNull(categories) && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (Objects.nonNull(rangeStart) && !rangeStart.isEmpty()) {
                start = LocalDateTime.parse(rangeStart, TIME_FORMATTER);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start));
            }

            if (Objects.nonNull(rangeEnd) && !rangeEnd.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(rangeEnd, TIME_FORMATTER);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
            }
            query.orderBy(criteriaBuilder.asc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Event> getSpecificationForEventPublic(String text, List<Long> categories, Boolean
            paid, String rangeStart, String rangeEnd, Boolean onlyAvailable) {

        return (root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(text) && !text.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                        "%" + text.toLowerCase() + "%"));
            }

            if (Objects.nonNull(categories) && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (Objects.nonNull(paid)) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }

            if ((rangeStart == null || rangeStart.isEmpty()) && (rangeEnd == null || rangeEnd.isEmpty())) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now()));
            } else {
                if (rangeStart != null && !rangeStart.isEmpty()) {
                    LocalDateTime start = LocalDateTime.parse(rangeStart, TIME_FORMATTER);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start));
                }
                if (rangeEnd != null && !rangeEnd.isEmpty()) {
                    LocalDateTime end = LocalDateTime.parse(rangeEnd, TIME_FORMATTER);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
                }
            }

            if (Objects.nonNull(onlyAvailable)) {
                if (onlyAvailable) {
                    predicates.add(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
                }
            }

            predicates.add(criteriaBuilder.equal(root.get("state"), State.PUBLISHED));

            query.orderBy(criteriaBuilder.asc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Transactional
    protected EventRequestStatusUpdateResult confirmRequests(Event event, List<EventRequest> requests) {
        Long confirmedRequests = event.getConfirmedRequests();
        Long participantLimit = event.getParticipantLimit();
        ArrayList<ParticipationRequestDto> confirmed = new ArrayList<>();
        ArrayList<ParticipationRequestDto> rejected = new ArrayList<>();

        int i = 0;
        while (confirmedRequests < participantLimit && i <= (requests.size() - 1)) {
            requests.get(i).setStatus(RequestStatus.CONFIRMED);
            confirmed.add(EventRequestDtoMapper.toDto(requests.get(i)));
            i++;
            confirmedRequests++;
        }
        if (confirmedRequests.equals(participantLimit)) {
            while (i < (requests.size() - 1)) {
                requests.get(i).setStatus(RequestStatus.REJECTED);
                rejected.add(EventRequestDtoMapper.toDto(requests.get(i)));
                i++;
            }
        }
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Transactional
    protected EventRequestStatusUpdateResult rejectAll(List<EventRequest> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        List<EventRequest> eventRequests = requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(new ArrayList<>(), EventRequestDtoMapper.toDto(eventRequests));
    }

    @Transactional
    protected EventRequestStatusUpdateResult confirmAll(List<EventRequest> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
        List<EventRequest> eventRequests = requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(EventRequestDtoMapper.toDto(eventRequests), new ArrayList<>());
    }
}
