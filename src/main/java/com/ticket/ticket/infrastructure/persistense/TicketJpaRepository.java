package com.ticket.ticket.infrastructure.persistense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.ticket.domain.event.TicketState;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

  Optional<List<TicketEntity>> findByEventIdAndSituation(UUID value, TicketState situation);
}
