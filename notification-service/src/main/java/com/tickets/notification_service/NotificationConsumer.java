package com.tickets.notification_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final SentEmailRepository repository;

    public NotificationConsumer(SentEmailRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_CONFIRMED_QUEUE)
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Received order.confirmed for order {}", event.orderId());

        // idempotency check, "at-least-once" = this can arrive twice
        if (repository.existsById(event.orderId())) {
            log.warn("Email already sent for order {} — skipping duplicate", event.orderId());
            return;
        }

        // "send" the email structured log line
        log.info("{\"event\":\"email_sent\",\"to\":\"user-{}\",\"template\":\"ticket_confirmation\",\"order_id\":{}}",
                event.userId(), event.orderId());

        repository.save(new SentEmail(event.orderId(), event.userId(), LocalDateTime.now()));
    }
}