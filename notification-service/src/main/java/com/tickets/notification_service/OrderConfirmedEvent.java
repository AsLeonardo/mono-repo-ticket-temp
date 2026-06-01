package com.tickets.notification_service;

import java.io.Serializable;

public record OrderConfirmedEvent(
    Long orderId,
    Long userId,
    Long eventId
) implements Serializable {}