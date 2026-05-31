package com.ticket.ticket.application.event.dto;

import com.ticket.ticket.domain.event.ID;
import com.ticket.ticket.infrastructure.json.IDDeserializer;

import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.UUID;

public record CreateTicketCommand(
    @JsonDeserialize(using = IDDeserializer.class) ID eventId,
    @JsonDeserialize(using = IDDeserializer.class) ID clientId) {
}
