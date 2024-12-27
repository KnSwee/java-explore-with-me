package ru.practicum.main.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.model.Category;
import ru.practicum.main.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("select e " +
            "from Event e " +
            "where e.initiator.id = :userId")
    List<Event> findByUser(@Param("userId") Long userId, PageRequest page);

    Optional<Event> findByInitiatorIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    Set<Event> findByIdIn(@Param("eventIds") Set<Long> eventIds);

    Boolean existsByCategory(Category category);
}