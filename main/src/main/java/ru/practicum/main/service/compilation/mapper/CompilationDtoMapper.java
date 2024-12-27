package ru.practicum.main.service.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.service.event.mapper.EventDtoMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationDtoMapper {


    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setEvents(events);
        return compilation;
    }

    public static CompilationDto toDto(Compilation comp) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(comp.getId());
        compilationDto.setTitle(comp.getTitle());
        compilationDto.setPinned(comp.getPinned());
        compilationDto.setEvents(EventDtoMapper.toEventShortDto(comp.getEvents()));
        return compilationDto;
    }

    public static List<CompilationDto> toDto(List<Compilation> comps) {
        List<CompilationDto> compilationDtos = new ArrayList<>();
        for (Compilation c : comps) {
            compilationDtos.add(toDto(c));
        }
        return compilationDtos;
    }
}
