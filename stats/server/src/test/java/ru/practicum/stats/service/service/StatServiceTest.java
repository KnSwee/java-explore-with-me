package ru.practicum.stats.service.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;
import ru.practicum.stats.service.model.EndpointHit;
import ru.practicum.stats.service.repository.EndpointRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class StatServiceTest {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatService statService;
    private final EndpointRepository endpointRepository;
    LocalDateTime now = LocalDateTime.now();
    EndpointHitDto endpointHit;

    @BeforeEach
    void setUp() {
        endpointRepository.deleteAll();

        endpointHit = new EndpointHitDto(1L, "applic", "uri", "1.2.345", now.format(TIME_FORMATTER));
    }

    @Test
    void saveEndpointHit() {
        statService.saveHit(endpointHit);

        List<EndpointHit> all = endpointRepository.findAll();
        EndpointHit savedEndpointHit = all.get(0);

        assertThat(all.size(), is(1));
        assertThat(savedEndpointHit.getIp(), equalTo("1.2.345"));
        assertThat(savedEndpointHit.getUri(), equalTo("uri"));
        assertThat(savedEndpointHit.getTimestamp().format(TIME_FORMATTER), equalTo(now.format(TIME_FORMATTER)));
        assertThat(savedEndpointHit.getApp(), equalTo("applic"));
    }

    @Test
    void getViewStats() {
        endpointRepository.save(new EndpointHit(1L, "applic", "uri", "1.2.345",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(2L, "applic", "uri", "1.2.346",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(3L, "applic", "uri2", "1.2.346",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(4L, "applic2", "uri3", "1.2.347",
                LocalDateTime.parse("2025-06-17 12:00:00", TIME_FORMATTER)));

        List<ViewStatsDto> viewStats = statService.getViewStats(
                "2024-12-15 12:00:00", "2024-12-20 12:00:00", List.of("uri", "uri2"), false);

        assertThat(viewStats.size(), is(2));
        assertThat(viewStats.get(0).getUri(), equalTo("uri"));
        assertThat(viewStats.get(1).getUri(), equalTo("uri2"));
        assertThat(viewStats.get(0).getHits(), equalTo(2L));
    }

    @Test
    void getUniqueViewStats() {
        endpointRepository.save(new EndpointHit(1L, "applic", "uri", "1.2.345",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(2L, "applic", "uri", "1.2.345",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(3L, "applic", "uri2", "1.2.345",
                LocalDateTime.parse("2024-12-17 12:00:00", TIME_FORMATTER)));
        endpointRepository.save(new EndpointHit(4L, "applic2", "uri3", "1.2.345",
                LocalDateTime.parse("2025-06-17 12:00:00", TIME_FORMATTER)));

        List<ViewStatsDto> viewStats = statService.getViewStats(
                "2024-12-15 12:00:00", "2024-12-20 12:00:00", List.of("uri", "uri2"), true);

        assertThat(viewStats.size(), is(2));
        assertThat(viewStats.get(0).getUri(), equalTo("uri"));
        assertThat(viewStats.get(1).getUri(), equalTo("uri2"));
        assertThat(viewStats.get(0).getHits(), equalTo(1L));
    }

}
