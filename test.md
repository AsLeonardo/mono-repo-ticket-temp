# create event with tickets
curl -X POST http://localhost:8080/events -H "Content-Type: application/json" -d "{\"name\":\"Show C\",\"eventDate\":\"2026-10-01T20:00:00\",\"price\":100.00,\"availableQuantity\":5}"

# create order (reserves a ticket)
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"

# pay which confirms the order AND publishes order.confirmed
curl -X POST http://localhost:8081/orders/1/pay -H "Content-Type: application/json" -d "{\"paymentMethod\":\"CREDIT_CARD\",\"amount\":100.00}"

# check the notification arrived async, through the broker
curl http://localhost:8082/notifications