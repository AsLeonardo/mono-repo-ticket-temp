package com.tickets.payment_gateway_mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/charges")
public class ChargeController {

    private static final Logger log = LoggerFactory.getLogger(ChargeController.class);

    private final Map<String, ChargeResponse> idempotencyStore = new ConcurrentHashMap<>();

    private final GatewayConfig config;

    public ChargeController(GatewayConfig config) {
        this.config = config;
    }

    @PostMapping
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest req) throws InterruptedException {

        // idempotency = same key returns same result
        if (req.idempotencyKey() != null && idempotencyStore.containsKey(req.idempotencyKey())) {
            log.info("Idempotent hit for key {}", req.idempotencyKey());
            return ResponseEntity.ok(idempotencyStore.get(req.idempotencyKey()));
        }

        // simulate latency
        Thread.sleep(config.latencyMs);

        // simulate technical failure (503)
        if (Math.random() < config.failureRate) {
            log.warn("Simulating technical failure for order {}", req.orderId());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ChargeResponse(null, "ERROR", req.idempotencyKey()));
        }

        // simulate business decline (different from technical failure)
        if (Math.random() < config.declineRate) {
            log.warn("Simulating business decline for order {}", req.orderId());
            ChargeResponse declined = new ChargeResponse(
                UUID.randomUUID().toString(), "DECLINED", req.idempotencyKey());
            if (req.idempotencyKey() != null) idempotencyStore.put(req.idempotencyKey(), declined);
            return ResponseEntity.ok(declined);
        }

        // happy path
        ChargeResponse approved = new ChargeResponse(
            UUID.randomUUID().toString(), "APPROVED", req.idempotencyKey());

        if (req.idempotencyKey() != null) idempotencyStore.put(req.idempotencyKey(), approved);

        log.info("Charge approved for order {}, chargeId {}", req.orderId(), approved.chargeId());
        return ResponseEntity.ok(approved);
    }
}