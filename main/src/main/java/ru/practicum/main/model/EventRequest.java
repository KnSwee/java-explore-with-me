package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.enums.RequestStatus;

import java.time.LocalDateTime;

@Table(name = "event_requests")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class EventRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

}
