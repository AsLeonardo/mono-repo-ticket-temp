package com.tickets.event_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequest(
    String name,
    LocalDateTime eventDate,
    BigDecimal price,
    Integer availableQuantity
) {}