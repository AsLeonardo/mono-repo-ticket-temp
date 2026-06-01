package com.tickets.order_service.order;

import java.io.Serializable;

public record OrderConfirmedEvent(
    Long orderId,
    Long userId,
    Long eventId
) implements Serializable {}