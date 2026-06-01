package com.tickets.order_service.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final EventServiceClient eventServiceClient;

    @Value("${reservation.ttl-minutes}")
    private int ttlMinutes;

    public OrderService(OrderRepository orderRepository,
                        EventServiceClient eventServiceClient) {
        this.orderRepository = orderRepository;
        this.eventServiceClient = eventServiceClient;
    }

    @Transactional
    public Order createOrder(Long userId, Long eventId) {
        // 1 call event-service to atomically decrement inventory
        eventServiceClient.reserveTicket(eventId);

        // 2 only if that succeeded, persist the order
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(userId, eventId, now, now.plusMinutes(ttlMinutes));
        Order saved = orderRepository.save(order);

        log.info("Order {} created for user {} on event {}, expires at {}",
                saved.getId(), userId, eventId, saved.getExpiresAt());

        return saved;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // runs every 60 seconds finding PENDING orders past their TTL and marks them EXPIRED
    // this does NOT release inventory yet, that's only when RabbitMQ is set
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expireStaleReservations() {
        List<Order> stale = orderRepository.findByStatusAndExpiresAtBefore(
                Order.Status.PENDING, LocalDateTime.now());

        if (!stale.isEmpty()) {
            log.warn("Expiring {} stale reservations", stale.size());
            stale.forEach(o -> o.setStatus(Order.Status.EXPIRED));
            orderRepository.saveAll(stale);
        }
    }
}