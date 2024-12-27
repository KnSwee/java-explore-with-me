package ru.practicum.main.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", defaultValue = "false") boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    private CompilationDto getCompilation(@PathVariable(name = "compId") Long compId) {
        return compilationService.getById(compId);
    }


}
