# Start it
docker compose up --build

# Prove load balancing:
curl http://localhost:8000/whoami \
curl http://localhost:8000/whoami

# 2. Register a user:
curl -X POST http://localhost:8000/users -H "Content-Type: application/json" -d "{\"email\":\"leo@test.com\",\"name\":\"Leonardo\"}"

# 3. Create an event:
curl -X POST http://localhost:8000/events -H "Content-Type: application/json" -d "{\"name\":\"Show D\",\"eventDate\":\"2026-11-01T20:00:00\",\"price\":120.00,\"availableQuantity\":3}"

# 4. List events (note the id, usage bellow):
curl http://localhost:8000/events

# 5. Create an order (reserve a ticket):
curl -X POST http://localhost:8000/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"

# 6. Pay (confirms order + publishes order.confirmed):
curl -X POST http://localhost:8000/orders/1/pay -H "Content-Type: application/json" -d "{\"paymentMethod\":\"CREDIT_CARD\",\"amount\":120.00}"

# 7. Check the async notification arrived:
curl http://localhost:8000/notifications

# Circuit breaker demo (admin endpoint not routed through gateway, use 8090)
<mark>100% failure</mark> \
curl -X POST http://localhost:8090/admin/mode -H "Content-Type: application/json" -d "{\"failureRate\":1.0}"

<mark>watch retries then breaker opening in logs</mark> \
curl -X POST http://localhost:8000/orders/1/pay -H "Content-Type: application/json" -d "{\"paymentMethod\":\"CREDIT_CARD\",\"amount\":120.00}"

<mark>recover</mark> \
curl -X POST http://localhost:8090/admin/mode -H "Content-Type: application/json" -d "{\"failureRate\":0.0}"

# Oversell prevention demo (create event with quantity 1, reserve it twice, second must 409):
curl -X POST http://localhost:8000/events -H "Content-Type: application/json" -d "{\"name\":\"Soldout\",\"eventDate\":\"2026-12-01T20:00:00\",\"price\":50.00,\"availableQuantity\":1}" 

curl -X POST http://localhost:8000/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"

curl -X POST http://localhost:8000/orders -H "Content-Type: application/json" -d "{\"userId\":1,\"eventId\":1}"

# http://localhost:8000 -> API Gateway (all app traffic)
# http://localhost:9090/targets -> Prometheus (confirm all targets UP)
# http://localhost:3000 -> Grafana (admin/admin)
# http://localhost:15672 -> RabbitMQ management (tickets/tickets)

docker compose psㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ# status of all containers       \
docker compose logs -f order-serviceㅤㅤㅤ# follow one service's logs  \
docker compose logs -f payment-gateway-mock                            \
docker compose downㅤㅤㅤㅤㅤㅤㅤㅤㅤ # stop everything                  \
docker compose down -vㅤㅤㅤㅤㅤㅤㅤㅤ# stop + wipe volumes (fresh DB)


```
ticket-system
├─ api-gateway
│  ├─ .mvn
│  │  └─ wrapper
│  │     └─ maven-wrapper.properties
│  ├─ Dockerfile
│  ├─ HELP.md
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ tickets
│     │  │        └─ api_gateway
│     │  │           └─ ApiGatewayApplication.java
│     │  └─ resources
│     │     ├─ application.properties
│     │     └─ application.yml
│     └─ test
│        └─ java
│           └─ com
│              └─ tickets
│                 └─ api_gateway
│                    └─ ApiGatewayApplicationTests.java
├─ docker-compose.yml
├─ event-service
│  ├─ .mvn
│  │  └─ wrapper
│  │     └─ maven-wrapper.properties
│  ├─ Dockerfile
│  ├─ HELP.md
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ tickets
│  │  │  │        └─ event_service
│  │  │  │           ├─ event
│  │  │  │           │  ├─ CreateEventRequest.java
│  │  │  │           │  ├─ Event.java
│  │  │  │           │  ├─ EventController.java
│  │  │  │           │  ├─ EventRepository.java
│  │  │  │           │  ├─ EventService.java
│  │  │  │           │  └─ InsufficientInventoryException.java
│  │  │  │           ├─ EventServiceApplication.java
│  │  │  │           ├─ GlobalExceptionHandler.java
│  │  │  │           ├─ HealthController.java
│  │  │  │           └─ InstanceController.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     └─ application.yml
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ tickets
│  │              └─ event_service
│  │                 └─ EventServiceApplicationTests.java
│  └─ target
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ application.yml
│     │  └─ com
│     │     └─ tickets
│     │        └─ event_service
│     │           ├─ event
│     │           │  ├─ CreateEventRequest.class
│     │           │  ├─ Event.class
│     │           │  ├─ EventController.class
│     │           │  ├─ EventRepository.class
│     │           │  ├─ EventService.class
│     │           │  └─ InsufficientInventoryException.class
│     │           ├─ EventServiceApplication.class
│     │           ├─ GlobalExceptionHandler.class
│     │           ├─ HealthController.class
│     │           └─ InstanceController.class
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     └─ test-classes
│        └─ com
│           └─ tickets
│              └─ event_service
│                 └─ EventServiceApplicationTests.class
├─ nginx
│  └─ nginx.conf
├─ notification-service
│  ├─ .mvn
│  │  └─ wrapper
│  │     └─ maven-wrapper.properties
│  ├─ Dockerfile
│  ├─ HELP.md
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ tickets
│  │  │  │        └─ notification_service
│  │  │  │           ├─ NotificationConsumer.java
│  │  │  │           ├─ NotificationController.java
│  │  │  │           ├─ NotificationServiceApplication.java
│  │  │  │           ├─ OrderConfirmedEvent.java
│  │  │  │           ├─ RabbitConfig.java
│  │  │  │           ├─ SentEmail.java
│  │  │  │           └─ SentEmailRepository.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     ├─ application.yml
│  │  │     ├─ static
│  │  │     └─ templates
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ tickets
│  │              └─ notification_service
│  │                 └─ NotificationServiceApplicationTests.java
│  └─ target
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ application.yml
│     │  └─ com
│     │     └─ tickets
│     │        └─ notification_service
│     │           ├─ NotificationConsumer.class
│     │           ├─ NotificationController.class
│     │           ├─ NotificationServiceApplication.class
│     │           ├─ OrderConfirmedEvent.class
│     │           ├─ RabbitConfig.class
│     │           ├─ SentEmail.class
│     │           └─ SentEmailRepository.class
│     └─ test-classes
│        └─ com
│           └─ tickets
│              └─ notification_service
│                 └─ NotificationServiceApplicationTests.class
├─ old.md
├─ order-service
│  ├─ .mvn
│  │  └─ wrapper
│  │     └─ maven-wrapper.properties
│  ├─ Dockerfile
│  ├─ HELP.md
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ tickets
│  │  │  │        └─ order_service
│  │  │  │           ├─ GlobalExceptionHandler.java
│  │  │  │           ├─ order
│  │  │  │           │  ├─ CreateOrderRequest.java
│  │  │  │           │  ├─ EventServiceClient.java
│  │  │  │           │  ├─ GatewayUnavailableException.java
│  │  │  │           │  ├─ InsufficientInventoryException.java
│  │  │  │           │  ├─ InvalidOrderStateException.java
│  │  │  │           │  ├─ Order.java
│  │  │  │           │  ├─ OrderConfirmedEvent.java
│  │  │  │           │  ├─ OrderController.java
│  │  │  │           │  ├─ OrderNotFoundException.java
│  │  │  │           │  ├─ OrderRepository.java
│  │  │  │           │  ├─ OrderService.java
│  │  │  │           │  ├─ PaymentDeclinedException.java
│  │  │  │           │  ├─ PaymentService.java
│  │  │  │           │  └─ RabbitConfig.java
│  │  │  │           ├─ OrderServiceApplication.java
│  │  │  │           └─ user
│  │  │  │              ├─ User.java
│  │  │  │              ├─ UserController.java
│  │  │  │              ├─ UserRepository.java
│  │  │  │              └─ UserService.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     └─ application.yml
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ tickets
│  │              └─ order_service
│  │                 └─ OrderServiceApplicationTests.java
│  └─ target
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ application.yml
│     │  └─ com
│     │     └─ tickets
│     │        └─ order_service
│     │           ├─ GlobalExceptionHandler.class
│     │           ├─ order
│     │           │  ├─ CreateOrderRequest.class
│     │           │  ├─ EventServiceClient.class
│     │           │  ├─ GatewayUnavailableException.class
│     │           │  ├─ InsufficientInventoryException.class
│     │           │  ├─ InvalidOrderStateException.class
│     │           │  ├─ Order$Status.class
│     │           │  ├─ Order.class
│     │           │  ├─ OrderConfirmedEvent.class
│     │           │  ├─ OrderController.class
│     │           │  ├─ OrderNotFoundException.class
│     │           │  ├─ OrderRepository.class
│     │           │  ├─ OrderService.class
│     │           │  ├─ PaymentDeclinedException.class
│     │           │  ├─ PaymentService.class
│     │           │  └─ RabbitConfig.class
│     │           ├─ OrderServiceApplication.class
│     │           └─ user
│     │              ├─ User.class
│     │              ├─ UserController.class
│     │              ├─ UserRepository.class
│     │              └─ UserService.class
│     ├─ generated-sources
│     │  └─ annotations
│     ├─ generated-test-sources
│     │  └─ test-annotations
│     └─ test-classes
│        └─ com
│           └─ tickets
│              └─ order_service
│                 └─ OrderServiceApplicationTests.class
├─ payment-gateway-mock
│  ├─ .mvn
│  │  └─ wrapper
│  │     └─ maven-wrapper.properties
│  ├─ Dockerfile
│  ├─ HELP.md
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ src
│  │  ├─ main
│  │  │  ├─ java
│  │  │  │  └─ com
│  │  │  │     └─ tickets
│  │  │  │        └─ payment_gateway_mock
│  │  │  │           ├─ AdminController.java
│  │  │  │           ├─ ChargeController.java
│  │  │  │           ├─ ChargeRequest.java
│  │  │  │           ├─ ChargeResponse.java
│  │  │  │           ├─ GatewayConfig.java
│  │  │  │           └─ PaymentGatewayMockApplication.java
│  │  │  └─ resources
│  │  │     ├─ application.properties
│  │  │     └─ application.yml
│  │  └─ test
│  │     └─ java
│  │        └─ com
│  │           └─ tickets
│  │              └─ payment_gateway_mock
│  │                 └─ PaymentGatewayMockApplicationTests.java
│  └─ target
│     ├─ classes
│     │  ├─ application.properties
│     │  ├─ application.yml
│     │  └─ com
│     │     └─ tickets
│     │        └─ payment_gateway_mock
│     │           ├─ AdminController.class
│     │           ├─ ChargeController.class
│     │           ├─ ChargeRequest.class
│     │           ├─ ChargeResponse.class
│     │           ├─ GatewayConfig.class
│     │           └─ PaymentGatewayMockApplication.class
│     └─ test-classes
│        └─ com
│           └─ tickets
│              └─ payment_gateway_mock
│                 └─ PaymentGatewayMockApplicationTests.class
├─ prometheus
│  └─ prometheus.yml
└─ README.md

```