package ru.practicum.stats.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;
import ru.practicum.stats.service.exception.BadRequestException;
import ru.practicum.stats.service.mapper.EndpointHitMapper;
import ru.practicum.stats.service.repository.EndpointRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EndpointRepository endpointRepository;


    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toEndpointHitDto(
                endpointRepository.save(
                        EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, TIME_FORMATTER);
        if (endTime.isBefore(startTime)) {
            throw new BadRequestException("End time is before start time");
        }
        if (unique) {
            return endpointRepository.getUniqueViewStats(startTime, endTime, uris);
        }
        return endpointRepository.getViewStats(startTime, endTime, uris);
    }
}
