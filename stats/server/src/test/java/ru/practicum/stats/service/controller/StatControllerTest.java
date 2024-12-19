package ru.practicum.stats.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.stats.dto.model.EndpointHitDto;
import ru.practicum.stats.dto.model.ViewStatsDto;
import ru.practicum.stats.service.service.StatService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StatControllerTest {

    @Mock
    private StatService statService;

    @InjectMocks
    private StatController statController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    EndpointHitDto endpointHitDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statController).build();
        endpointHitDto = new EndpointHitDto(1L, "app", "uri", "ip", "2024-12-17 12:00:00");
    }

    @Test
    void saveEndpointHitTest() throws Exception {
        when(statService.saveHit(any())).thenReturn(endpointHitDto);

        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(endpointHitDto))
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.app", is("app")))
                .andExpect(jsonPath("$.uri", is("uri")))
                .andExpect(jsonPath("$.ip", is("ip")))
                .andExpect(jsonPath("$.timestamp", is("2024-12-17 12:00:00")));
    }

    @Test
    void getStatsTest() throws Exception {
        when(statService.getViewStats(anyString(), anyString(), any(), anyBoolean())).thenReturn(List.of(
                new ViewStatsDto("app", "uri1", 2L),
                new ViewStatsDto("app", "uri2", 3L)));

        mockMvc.perform(get("/stats")
                        .param("start", "2024-12-12 12:00:00")
                        .param("end", "2024-12-20 12:00:00")
                        .param("uris", List.of("uri1", "uri2").toString())
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is("app")))
                .andExpect(jsonPath("$[0].uri", is("uri1")))
                .andExpect(jsonPath("$[0].hits", is(2)))
                .andExpect(jsonPath("$[1].app", is("app")))
                .andExpect(jsonPath("$[1].uri", is("uri2")))
                .andExpect(jsonPath("$[1].hits", is(3)));
    }

}
