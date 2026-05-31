package com.ticket.ticket.application.event;

import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.domain.event.TicketRepository;
import com.ticket.ticket.domain.exceptions.DomainException;
import com.ticket.ticket.application.event.dto.TicketResponse;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ticket.ticket.domain.event.TicketRepository;

@Repository
public class PayTicketUseCase {

  private final TicketRepository ticketRepository;

  public PayTicketUseCase(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public TicketResponse execute(UUID idTicket) {
    TicketId id = new TicketId(idTicket);
    Optional<Ticket> possibleTicket = ticketRepository.findBy(id);
    if (possibleTicket.isEmpty())
      throw new DomainException.AttemptToPayANonExistentTicket(id);
    Ticket ticket = possibleTicket.get();
    ticket.pay();
    ticketRepository.save(ticket);
    return TicketResponse.from(ticket);
  }
}
