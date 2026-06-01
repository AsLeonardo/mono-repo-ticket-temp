package com.tickets.notification_service;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final SentEmailRepository repository;

    public NotificationController(SentEmailRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SentEmail> all() {
        return repository.findAll();
    }
}