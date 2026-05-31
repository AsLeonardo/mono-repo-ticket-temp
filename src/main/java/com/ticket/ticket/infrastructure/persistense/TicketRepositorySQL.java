package com.ticket.ticket.infrastructure.persistense;
// infrastructure/persistence/TicketRepositoryImpl.java

import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.domain.event.TicketRepository;
import com.ticket.ticket.domain.event.TicketState;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketRepositorySQL implements TicketRepository {

  private final TicketJpaRepository jpa;

  public TicketRepositorySQL(TicketJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void save(Ticket ticket) {
    jpa.save(TicketEntity.fromDomain(ticket));
  }

  @Override
  public Optional<Ticket> findBy(TicketId id) {
    return jpa.findById(id.value())
        .map(TicketEntity::toDomain);
  }

  @Override
  public List<Ticket> findActiveTicketBy(ID eventId) {
    return jpa.findByEventIdAndSituation(eventId.value(), TicketState.ATIVO)
        .get()
        .stream()
        .map(TicketEntity::toDomain)
        .toList();
  }

}
