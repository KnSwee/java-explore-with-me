package ru.practicum.stats.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.model.ViewStatsDto;
import ru.practicum.stats.service.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.stats.dto.model.ViewStatsDto(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and eh.uri in :uris " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    List<ViewStatsDto> getViewStats(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query("select new ru.practicum.stats.dto.model.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and eh.uri in :uris " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc")
    List<ViewStatsDto> getUniqueViewStats(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);
}
