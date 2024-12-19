package ru.practicum.main.service.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.model.EventRequest;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventRequestDtoMapper {

    public static ParticipationRequestDto toDto(EventRequest er) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(er.getId());
        participationRequestDto.setCreated(er.getCreated().format(TIME_FORMATTER));
        participationRequestDto.setRequester(er.getRequester().getId());
        participationRequestDto.setEvent(er.getEvent().getId());
        participationRequestDto.setStatus(er.getStatus().name());
        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> toDto(List<EventRequest> er) {
        List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
        for (EventRequest e : er) {
            participationRequestDtos.add(toDto(e));
        }
        return participationRequestDtos;
    }

}
