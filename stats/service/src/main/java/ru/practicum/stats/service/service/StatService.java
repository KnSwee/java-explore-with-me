package ru.practicum.stats.service.service;

import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;

import java.util.List;

public interface StatService {

    EndpointHitDto saveHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getViewStats(String start, String end, List<String> uris, boolean unique);

}
