package ru.practicum.main.service.request;

import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
