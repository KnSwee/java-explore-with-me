package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.model.EventRequest;

import java.util.List;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    @Query("select er " +
            "from EventRequest er " +
            "where er.requester.id = :userId and " +
            "er.event.id = :eventId")
    EventRequest findByRequesterAndEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("select er " +
            "from EventRequest er " +
            "where er.requester.id = :userId")
    List<EventRequest> findByRequesterId(@Param("userId") Long userId);

    List<EventRequest> findByIdIn(List<Long> ids);

    List<EventRequest> findByEventId(Long eventId);
}