package com.ticket.ticket.application.event;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import java.util.List;

import com.ticket.ticket.application.event.dto.TicketResponse;
import com.ticket.ticket.domain.event.Ticket;
import com.ticket.ticket.domain.event.TicketRepository;
import com.ticket.ticket.domain.event.TicketState;
import com.ticket.ticket.domain.event.ID;

@Service
public class ViewActiveTicketUseCase {

  private final TicketRepository ticketRepository;

  public ViewActiveTicketUseCase(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public List<TicketResponse> execute(
      UUID eventId) {
    List<Ticket> foundTickets = ticketRepository.findActiveTicketBy(new ID(eventId));
    if (foundTickets.isEmpty())
      return List.of();
    Stream<Ticket> stream = foundTickets.size() > 1000
        ? foundTickets.parallelStream()
        : foundTickets.stream();
    return stream.map(TicketResponse::from).toList();
  }
}
