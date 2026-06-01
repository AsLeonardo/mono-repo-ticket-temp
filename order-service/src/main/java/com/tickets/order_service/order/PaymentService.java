package com.tickets.order_service.order;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Value("${payment-gateway.url}")
    private String gatewayUrl;

private final RabbitTemplate rabbitTemplate;

public PaymentService(
    RestTemplate restTemplate,
    OrderRepository orderRepository,
    RabbitTemplate rabbitTemplate) {
    this.restTemplate = restTemplate;
    this.orderRepository = orderRepository;
    this.rabbitTemplate = rabbitTemplate;
}

    @CircuitBreaker(name = "gateway", fallbackMethod = "chargeFallback")
    @Retry(name = "gateway")
    public void processPayment(Long orderId, String paymentMethod, BigDecimal amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        String idempotencyKey = "order-" + orderId;

        Map<String, Object> request = Map.of(
            "idempotencyKey", idempotencyKey,
            "paymentMethod", paymentMethod,
            "orderId", orderId,
            "amount", amount
        );

        try {
            var response = restTemplate.postForEntity(
                gatewayUrl + "/charges", request, Map.class);

            String status = (String) response.getBody().get("status");

            if ("APPROVED".equals(status)) {
                order.setStatus(Order.Status.CONFIRMED);
                orderRepository.save(order);
                log.info("Payment approved for order {}", orderId);

                // publish async event — fire and forget into the broker
                OrderConfirmedEvent event = new OrderConfirmedEvent(
                    order.getId(), order.getUserId(), order.getEventId());
                rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.ORDER_CONFIRMED_ROUTING_KEY,
                    event);
                log.info("Published order.confirmed for order {}", orderId);
            } else if ("DECLINED".equals(status)) {
                order.setStatus(Order.Status.FAILED);
                orderRepository.save(order);
                throw new PaymentDeclinedException(orderId);
            }

        } catch (HttpServerErrorException ex) {
            log.error("Gateway technical failure for order {}: {}", orderId, ex.getMessage());
            throw ex; // let Retry handle it
        }
    }

    // called when circuit breaker is OPEN — gateway is down, fail fast
    public void chargeFallback(Long orderId, String paymentMethod, BigDecimal amount, Exception ex) {
        log.error("Circuit breaker OPEN — fallback for order {}. Cause: {}", orderId, ex.getMessage());
        throw new RuntimeException("Payment service unavailable. Try again later.");
    }
}