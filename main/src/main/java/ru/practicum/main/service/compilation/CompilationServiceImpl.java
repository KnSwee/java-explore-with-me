package ru.practicum.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationDto;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.model.Event;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.compilation.mapper.CompilationDtoMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;


    @Override
    public List<CompilationDto> getCompilations(boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);

        return CompilationDtoMapper.toDto(compilations);
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ElementNotFoundException("Подборка не найдена или недоступна"));
        return CompilationDtoMapper.toDto(compilation);
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            Compilation comp = compilationRepository.save(CompilationDtoMapper.toCompilation(newCompilationDto, new HashSet<>()));
            return CompilationDtoMapper.toDto(comp);
        }

        Set<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        Compilation compilation = compilationRepository.save(CompilationDtoMapper.toCompilation(newCompilationDto, events));
        return CompilationDtoMapper.toDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
        } else {
            throw new ElementNotFoundException("Подборка не найдена или недоступна");
        }

    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ElementNotFoundException("Подборка не найдена или недоступна"));
        if (newCompilationDto.getEvents() != null) {
            if (!newCompilationDto.getEvents().isEmpty()) {
                Set<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
                compilation.setEvents(events);
            }
        }
        if (newCompilationDto.getTitle() != null) {
            if (!newCompilationDto.getTitle().isEmpty()) {
                compilation.setTitle(newCompilationDto.getTitle());
            }
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        Compilation comp = compilationRepository.save(compilation);

        return CompilationDtoMapper.toDto(comp);
    }
}
