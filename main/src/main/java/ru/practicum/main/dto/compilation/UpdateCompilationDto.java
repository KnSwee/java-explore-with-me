package ru.practicum.main.dto.compilation;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.util.NotBlankAnnotation;

import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class UpdateCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    @NotBlankAnnotation
    @Length(min = 1, max = 50)
    String title;

}
