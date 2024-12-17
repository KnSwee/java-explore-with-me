package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.model.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatClient extends BaseClient {

    public StatClient(@Value(value = "${stats.url}") String url, RestTemplateBuilder restTemplateBuilder) {
        super(
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build());
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> params = Map.of("start", start,
                "end", end,
                "uris", uris,
                "unique", unique);

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", params);
    }

    public ResponseEntity<Object> save(EndpointHitDto endpointHitDto) {
        return post("/hit", null, endpointHitDto);
    }
}
