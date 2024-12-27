package ru.practicum.main.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.Location;
import ru.practicum.main.util.NotBlankAnnotation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @NotBlankAnnotation
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @NotBlankAnnotation
    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String stateAction;

    @NotBlankAnnotation
    @Size(min = 3, max = 120)
    private String title;

}
