package com.tickets.event_service.event;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(Long eventId) {
        super("No tickets available for event: " + eventId);
    }
}