package com.tickets.event_service.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody CreateEventRequest req) {
        Event event = new Event(req.name(), req.eventDate(), req.price(), req.availableQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(event));
    }

    @GetMapping
    public List<Event> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Event findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/quantity")
    public Event updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        return service.updateQuantity(id, body.get("quantity"));
    }

    @PatchMapping("/{id}/price")
    public Event updatePrice(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        return service.updatePrice(id, body.get("price"));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long id) {
        service.reserveTicket(id);
        return ResponseEntity.ok().build();
    }
}