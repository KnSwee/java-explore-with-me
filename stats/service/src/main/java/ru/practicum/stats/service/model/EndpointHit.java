package ru.practicum.stats.service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "endpoint_hit")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app")
    private String app;

    @Column(name = "uri")
    private String uri;

    @Column(name = "ip")
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}
