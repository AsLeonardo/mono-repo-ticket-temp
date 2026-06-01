package com.tickets.event_service.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Modifying
    @Query("UPDATE Event e SET e.availableQuantity = e.availableQuantity - 1 " +
            "WHERE e.id = :id AND e.availableQuantity > 0")
    int decrementQuantity(@Param("id") Long id);
}