package com.ticket.ticket.application.event.dto;

import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.domain.event.TicketId;
import com.ticket.ticket.infrastructure.json.IDDeserializer;

import tools.jackson.databind.annotation.JsonDeserialize;

public record CancelTicketCommand(
    @JsonDeserialize(using = IDDeserializer.class) TicketId ticketId) {
}
