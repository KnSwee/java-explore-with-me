package ru.practicum.main.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.eventRequest.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getPrivateEvents(@PathVariable(name = "userId") Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getPrivateEvents(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto createEvent(@PathVariable(name = "userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByCreator(@PathVariable(name = "userId") Long userId,
                                          @PathVariable(name = "eventId") Long eventId) {
        return eventService.getEventByCreator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable(name = "userId") Long userId,
                                   @PathVariable(name = "eventId") Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.patchPrivateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsByUser(@PathVariable(name = "userId") Long userId,
                                                                @PathVariable(name = "eventId") Long eventId) {
        return eventService.getEventRequestsByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchEventRequestsByUser(@PathVariable(name = "userId") Long userId,
                                                                   @PathVariable(name = "eventId") Long eventId,
                                                                   @RequestBody EventRequestStatusUpdateRequest requestUpdate) {
        return eventService.patchEventRequestsByUser(userId, eventId, requestUpdate);
    }


}
