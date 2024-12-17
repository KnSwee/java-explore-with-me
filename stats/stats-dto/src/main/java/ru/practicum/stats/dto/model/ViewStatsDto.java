package ru.practicum.stats.dto.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {

    private String app;
    private String uri;
    private Long hits;

}
