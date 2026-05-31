// infrastructure/persistence/TicketEntity.java
package com.ticket.ticket.infrastructure.persistense;

import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.TicketState;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class TicketEntity {

  @Id
  private UUID id;

  @Column(name = "event_id", nullable = false)
  private UUID eventId;

  @Column(name = "client_id", nullable = false)
  private UUID clientId;

  @Enumerated(EnumType.STRING)
  @Column(name = "situation", nullable = false)
  private TicketState situation;

  // @ManyToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name = "event_id", insertable = false, updatable = false)
  // private EventEntity event;

  // domínio → JPA
  public static TicketEntity fromDomain(Ticket ticket) {
    TicketEntity entity = new TicketEntity();
    entity.id = ticket.getId().value();
    entity.eventId = ticket.getEventId().value();
    entity.clientId = ticket.getClientId().value();
    entity.situation = ticket.getSituation();
    return entity;
  }

  // JPA → domínio
  public Ticket toDomain() {
    return new Ticket(
        situation,
        new TicketId(id),
        new ID(eventId),
        new ID(clientId));
  }
}
