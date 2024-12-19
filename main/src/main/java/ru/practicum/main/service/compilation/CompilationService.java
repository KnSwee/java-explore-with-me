package ru.practicum.main.service.compilation;

import jakarta.validation.Valid;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);

    CompilationDto createCompilation(@Valid NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto updateCompilation(Long compId, @Valid UpdateCompilationDto newCompilationDto);
}
