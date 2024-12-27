package ru.practicum.main.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEventsAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                @RequestParam(name = "states", required = false) List<String> states,
                                                @RequestParam(name = "categories", required = false) List<Long> categories,
                                                @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.searchEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.patchAdminEvent(eventId, updateEventAdminRequest);
    }


}
