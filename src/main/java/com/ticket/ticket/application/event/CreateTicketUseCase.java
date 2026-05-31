package com.ticket.ticket.application.event;

import com.ticket.ticket.application.event.dto.CreateTicketCommand;
import com.ticket.ticket.application.event.dto.TicketResponse;
import com.ticket.ticket.domain.event.TicketRepository;
import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.domain.event.TicketState;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CreateTicketUseCase {

  private final TicketRepository ticketRepository;

  public CreateTicketUseCase(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public TicketResponse execute(CreateTicketCommand command) {
    // chama o domínio
    Ticket ticket = new Ticket(
        TicketState.ATIVO,
        new TicketId(UUID.randomUUID()),
        command.eventId(),
        command.clientId());

    // salva via repositório
    ticketRepository.save(ticket);

    return TicketResponse.from(ticket);
  }
}
