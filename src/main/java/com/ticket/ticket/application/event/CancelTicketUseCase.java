package com.ticket.ticket.application.event;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.ticket.application.event.dto.TicketResponse;
import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.domain.event.TicketRepository;
import com.ticket.ticket.domain.exceptions.DomainException;

@Service
public class CancelTicketUseCase {

  private final TicketRepository ticketRepository;

  public CancelTicketUseCase(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public TicketResponse execute(ID id) {
    Optional<Ticket> foundTicket = ticketRepository.findBy(new TicketId(id));
    if (foundTicket.isEmpty())
      throw new DomainException.AttemptToCancelANonExistentTicket(new TicketId(id));
    Ticket ticket = foundTicket.get();
    ticket.cancel();
    ticketRepository.save(ticket);
    return TicketResponse.from(ticket);
  }
}
