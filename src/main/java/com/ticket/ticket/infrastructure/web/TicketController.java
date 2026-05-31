package com.ticket.ticket.infrastructure.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.ticket.application.event.CancelTicketUseCase;
import com.ticket.ticket.application.event.CreatePayedTicketUseCase;
import com.ticket.ticket.application.event.ViewActiveTicketUseCase;
import com.ticket.ticket.application.event.dto.CreateTicketCommand;
import com.ticket.ticket.application.event.dto.TicketResponse;
import com.ticket.ticket.domain.event.ID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

  private final CreatePayedTicketUseCase createPayedTicketUseCase;
  private final CancelTicketUseCase cancelTicketUseCase;
  private final ViewActiveTicketUseCase viewActiveTicketUseCase;

  public TicketController(CreatePayedTicketUseCase createTicketUseCase, CancelTicketUseCase cancelTicketUseCase,
      ViewActiveTicketUseCase view) {
    this.createPayedTicketUseCase = createTicketUseCase;
    this.cancelTicketUseCase = cancelTicketUseCase;
    this.viewActiveTicketUseCase = view;
  }

  @PostMapping
  public ResponseEntity<TicketResponse> createPayedTicket(@RequestBody CreateTicketCommand command) {
    TicketResponse response = createPayedTicketUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // /{id}/cancel com ID na URL
  @PatchMapping("/{id}/cancel")
  public ResponseEntity<TicketResponse> cancel(@PathVariable UUID id) {
    TicketResponse response = cancelTicketUseCase.execute(new ID(id));
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping("/view/event/{eventId}/active")
  public ResponseEntity<List<TicketResponse>> activeTickets(@PathVariable UUID eventId) {
    List<TicketResponse> tickets = viewActiveTicketUseCase.execute(eventId);

    if (tickets.isEmpty()) {
      return ResponseEntity.noContent().build(); // 204
    }

    return ResponseEntity.ok(tickets); // 200
  }

}
