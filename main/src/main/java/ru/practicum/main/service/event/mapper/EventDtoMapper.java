package ru.practicum.main.service.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.enums.State;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.Location;
import ru.practicum.main.model.User;
import ru.practicum.main.service.category.mapper.CategoryDtoMapper;
import ru.practicum.main.service.user.mapper.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;
import static ru.practicum.main.util.DateTimeMapper.getDateTimeFromString;
import static ru.practicum.main.util.DateTimeMapper.getStringFromDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventDtoMapper {

    public static EventFullDto toEventDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(CategoryDtoMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(getStringFromDate(event.getCreatedOn()));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(getStringFromDate(event.getEventDate()));
        eventFullDto.setInitiator(UserDtoMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(new Location(event.getLatitude(), event.getLongitude()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(getStringFromDate(event.getPublishedOn()));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState().name());
        eventFullDto.setTitle(event.getTitle());

        return eventFullDto;
    }


    public static List<EventFullDto> toEventDto(List<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtos.add(toEventDto(event));
        }
        return eventFullDtos;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryDtoMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setEventDate(getStringFromDate(event.getEventDate()));
        eventShortDto.setInitiator(UserDtoMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        return eventShortDto;
    }

    public static List<EventShortDto> toEventShortDto(List<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtos.add(toEventShortDto(event));
        }
        return eventShortDtos;
    }

    public static Set<EventShortDto> toEventShortDto(Set<Event> events) {
        Set<EventShortDto> eventShortDtos = new HashSet<>();
        for (Event event : events) {
            eventShortDtos.add(toEventShortDto(event));
        }
        return eventShortDtos;
    }

    public static Event toEvent(EventFullDto eventFullDto, User user) {
        Event event = new Event();
        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(CategoryDtoMapper.toCategory(eventFullDto.getCategory()));
        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        event.setCreatedOn(LocalDateTime.parse(eventFullDto.getCreatedOn(), TIME_FORMATTER));
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventFullDto.getEventDate(), TIME_FORMATTER));
        event.setInitiator(user);
        event.setLatitude(eventFullDto.getLocation().getLat());
        event.setLongitude(eventFullDto.getLocation().getLon());
        event.setPaid(eventFullDto.getPaid());
        event.setParticipantLimit(eventFullDto.getParticipantLimit());
        event.setPublishedOn(LocalDateTime.parse(eventFullDto.getPublishedOn(), TIME_FORMATTER));
        event.setRequestModeration(eventFullDto.getRequestModeration());
        event.setState(State.valueOf(eventFullDto.getState()));
        event.setTitle(eventFullDto.getTitle());

        return event;
    }

    public static Event toEvent(NewEventDto newEventDto, Category category) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(getDateTimeFromString(newEventDto.getEventDate()));
        event.setLatitude(newEventDto.getLocation().getLat());
        event.setLongitude(newEventDto.getLocation().getLon());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        event.setTitle(newEventDto.getTitle());

        return event;
    }

}
