package com.tickets.order_service.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;
    private final PaymentService paymentService;

    public OrderController(OrderService service, PaymentService paymentService) {
        this.service = service;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestHeader("X-User-Id") Long userId,
                                        @RequestBody CreateOrderRequest req) {
        Order order = service.createOrder(userId, req.eventId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Order> findAll() {
        return service.findAll();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PaymentService.PaymentResult> pay(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        String method = (String) body.get("paymentMethod");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(paymentService.processPayment(id, method, amount));
    }
}