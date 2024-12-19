package ru.practicum.stats.service.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitMapper {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setId(endpointHit.getId());
        endpointHitDto.setIp(endpointHit.getIp());
        endpointHitDto.setTimestamp(endpointHit.getTimestamp().format(TIME_FORMAT));
        endpointHitDto.setApp(endpointHit.getApp());
        endpointHitDto.setUri(endpointHit.getUri());
        return endpointHitDto;
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(endpointHitDto.getId());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), TIME_FORMAT));
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        return endpointHit;
    }

    public static List<EndpointHit> toEndpointHit(List<EndpointHitDto> endpointHitDtos) {
        List<EndpointHit> endpointHits = new ArrayList<>();
        for (EndpointHitDto endpointHitDto : endpointHitDtos) {
            endpointHits.add(toEndpointHit(endpointHitDto));
        }
        return endpointHits;
    }

}
