package ru.practicum.stats.service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;
import ru.practicum.stats.service.service.StatService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService statService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public EndpointHitDto saveEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Saving endpoint hit for uri {}", endpointHitDto.getUri());
        return statService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    private List<ViewStatsDto> getViewStats(@RequestParam(name = "start", required = false) String start,
                                            @RequestParam(name = "end", required = false) String end,
                                            @RequestParam(name = "uris", required = false) List<String> uris,
                                            @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("Getting view stats for uri {}", uris);
        return statService.getViewStats(start, end, uris, unique);
    }


}
