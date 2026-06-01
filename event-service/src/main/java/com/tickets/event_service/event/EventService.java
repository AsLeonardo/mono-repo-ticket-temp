package com.tickets.event_service.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event create(Event event) {
        return repository.save(event);
    }

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    @Transactional
    public Event updateQuantity(Long id, Integer quantity) {
        Event event = findById(id);
        event.setAvailableQuantity(quantity);
        return repository.save(event);
    }

    @Transactional
    public Event updatePrice(Long id, BigDecimal price) {
        Event event = findById(id);
        event.setPrice(price);
        return repository.save(event);
    }

    @Transactional
    public void reserveTicket(Long id) {
        int affected = repository.decrementQuantity(id);
        if (affected == 0) {
            throw new InsufficientInventoryException(id);
        }
    }
}