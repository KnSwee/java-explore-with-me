package ru.practicum.main.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.State;
import ru.practicum.main.exception.DataConflictException;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventRequest;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.EventRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.request.mapper.EventRequestDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRequestRepository eventRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("Пользователя с id " + userId + " не существует");
        }
        List<EventRequest> requests = eventRequestRepository.findByRequesterId(userId);
        return EventRequestDtoMapper.toDto(requests);
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователя с id " + userId + " не существует"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ElementNotFoundException("События с id " + eventId + " не существует"));
        Long confirmedRequests = event.getConfirmedRequests();
        EventRequest duplicated = eventRequestRepository.findByRequesterAndEvent(userId, eventId);

        if (duplicated != null) {
            throw new DataConflictException("Запрос от пользователя с id " + userId + " на участие в событии с id "
                    + eventId + " уже существует");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new DataConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new DataConflictException("нельзя участвовать в неопубликованном событии");
        }

        if (event.getParticipantLimit() != 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new DataConflictException("Достигнут лимит запросов на участие для события с id " + eventId);
        }

        EventRequest eventRequest = new EventRequest();
        eventRequest.setCreated(LocalDateTime.now());
        eventRequest.setEvent(event);
        eventRequest.setRequester(user);
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            eventRequest.setStatus(RequestStatus.PENDING);
        } else {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests + 1);
        }

        eventRepository.save(event);
        EventRequest er = eventRequestRepository.save(eventRequest);
        return EventRequestDtoMapper.toDto(er);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("Пользователя с id " + userId + " не существует");
        }
        EventRequest request = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new ElementNotFoundException("Запроса на участие с id " + requestId + " не существует"));
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            throw new DataConflictException("Нельзя отменить принятую заявку");
        }
        request.setStatus(RequestStatus.CANCELED);
        EventRequest er = eventRequestRepository.save(request);
        return EventRequestDtoMapper.toDto(er);
    }
}
