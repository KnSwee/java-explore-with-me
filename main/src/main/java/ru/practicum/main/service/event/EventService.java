package ru.practicum.main.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByCreator(Long userId, Long eventId);

    EventFullDto patchPrivateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getEventRequestsByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchEventRequestsByUser(Long userId, Long eventId, EventRequestStatusUpdateRequest requestUpdate);

    List<EventFullDto> searchEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                         String rangeEnd, Integer from, Integer size);

    EventFullDto patchAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> searchPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                           String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getPublicEvent(Long id, HttpServletRequest request);
}
