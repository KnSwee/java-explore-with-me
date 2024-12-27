package ru.practicum.main.dto.eventRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;
    private String status;
}
