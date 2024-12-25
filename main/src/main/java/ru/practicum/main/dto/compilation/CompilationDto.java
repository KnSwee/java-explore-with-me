package ru.practicum.main.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.event.EventShortDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {


    private Set<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;

}
