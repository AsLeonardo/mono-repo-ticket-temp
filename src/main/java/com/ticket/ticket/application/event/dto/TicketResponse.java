
package com.ticket.ticket.application.event.dto;

import com.ticket.ticket.domain.event.Ticket;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    UUID eventId,
    UUID clientId,
    String situation // "ATIVO", "USADO", "CANCELADO"
) {

  public static TicketResponse from(Ticket ticket) {
    return new TicketResponse(
        ticket.getId().value(),
        ticket.getEventId().value(),
        ticket.getClientId().value(),
        ticket.getSituation().toString());
  }
}
