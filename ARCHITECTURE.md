# Event-Driven Microservices Platform Architecture

## Overview

This platform implements a **choreography-based saga pattern** using **Apache Kafka** for event-driven communication between microservices. The architecture is designed for high scalability, fault tolerance, and real-time transaction processing.

## 🏗️ Architecture Components

### Core Services

1. **Order Service** (Port: 8081)
   - Manages order lifecycle
   - Publishes `OrderCreatedEvent` to Kafka
   - Stores order data in Redis
   - REST API for order operations

2. **Inventory Service** (Port: 8082)
   - Manages product inventory
   - Listens to `OrderCreatedEvent`
   - Publishes `InventoryReservedEvent`
   - Implements inventory reservation logic

3. **Payment Service** (Port: 8083)
   - Processes payments
   - Listens to `InventoryReservedEvent`
   - Publishes `PaymentProcessedEvent`
   - Handles payment failures and rollbacks

4. **Notification Service** (Port: 8084)
   - Sends notifications
   - Listens to `PaymentProcessedEvent`
   - Publishes `NotificationEvent`
   - Handles email/SMS notifications

### Infrastructure Services

5. **Eureka Server** (Port: 8761)
   - Service discovery and registration
   - Health monitoring
   - Load balancing support

6. **API Gateway** (Port: 8080)
   - Single entry point for all APIs
   - Route management
   - CORS handling
   - Request/response transformation

### Data Stores

7. **Apache Kafka**
   - Event streaming platform
   - Topics: `order-events`, `inventory-events`, `payment-events`, `notification-events`
   - Ensures message durability and ordering

8. **Redis**
   - In-memory data store
   - Caches transient data (orders, inventory, payments)
   - Session management
   - Distributed locking

## 🔄 Event Flow & Saga Pattern

### Successful Order Flow

```
1. Client → API Gateway → Order Service
   POST /orders
   ↓
2. Order Service creates order in Redis
   ↓
3. Order Service publishes OrderCreatedEvent to Kafka
   ↓
4. Inventory Service consumes OrderCreatedEvent
   ↓
5. Inventory Service reserves stock in Redis
   ↓
6. Inventory Service publishes InventoryReservedEvent
   ↓
7. Payment Service consumes InventoryReservedEvent
   ↓
8. Payment Service processes payment
   ↓
9. Payment Service publishes PaymentProcessedEvent
   ↓
10. Notification Service consumes PaymentProcessedEvent
    ↓
11. Notification Service sends confirmation
```

### Rollback Flow (Saga Pattern)

```
If Payment fails:
1. Payment Service publishes PaymentProcessedEvent (success: false)
   ↓
2. Inventory Service consumes failed payment event
   ↓
3. Inventory Service releases reserved stock
   ↓
4. Order Service updates order status to "FAILED"
   ↓
5. Notification Service sends failure notification
```

## 📊 Data Flow Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │ API Gateway │    │   Services  │    │   Kafka     │
│             │    │             │    │             │    │             │
│  Web/Mobile │───▶│   Port 8080 │───▶│  Port 8081+ │───▶│   Port 9092 │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                              │
                                                              ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Eureka    │    │    Redis    │    │   Zookeeper │    │   Services  │
│             │    │             │    │             │    │             │
│  Port 8761  │    │  Port 6379  │    │  Port 2181  │    │  Event      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## 🔧 Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **Spring Cloud 2023.0** - Microservices framework
- **Spring Kafka** - Kafka integration
- **Spring Data Redis** - Redis integration

### Infrastructure
- **Apache Kafka 3.5** - Event streaming
- **Redis 7.2** - In-memory data store
- **Docker & Docker Compose** - Containerization
- **Zookeeper** - Kafka coordination

### Monitoring & Documentation
- **Spring Boot Actuator** - Health checks
- **Micrometer** - Metrics collection
- **OpenAPI/Swagger** - API documentation
- **Kafka UI** - Kafka monitoring

## 🚀 Deployment Architecture

### Container Orchestration
```
┌─────────────────────────────────────────────────────────────────┐
│                    Docker Compose Network                       │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │ Zookeeper│  │  Kafka  │  │  Redis  │  │ Eureka  │  │ Gateway │ │
│  │  2181   │  │  9092   │  │  6379   │  │  8761   │  │  8080   │ │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘ │
│                                                                 │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │  Order  │  │Inventory│  │ Payment │  │Notification│ │ Kafka UI│ │
│  │  8081   │  │  8082   │  │  8083   │  │  8084   │  │  8085   │ │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### Service Discovery
- **Eureka Server** registers all microservices
- **API Gateway** uses service discovery for routing
- **Load balancing** handled by Spring Cloud

## 🔒 Security Considerations

### Current Implementation
- Basic CORS configuration
- Input validation with Bean Validation
- Error handling with global exception handlers

### Production Recommendations
- **OAuth2/JWT** authentication
- **HTTPS/TLS** encryption
- **API rate limiting**
- **Circuit breakers** with Resilience4j
- **Distributed tracing** with Zipkin
- **Centralized logging** with ELK stack

## 📈 Scalability Features

### Horizontal Scaling
- **Stateless services** can be scaled independently
- **Kafka partitions** enable parallel processing
- **Redis clustering** for high availability
- **Load balancing** through Eureka

### Performance Optimizations
- **Redis caching** for frequently accessed data
- **Kafka batching** for high-throughput events
- **Connection pooling** for database connections
- **Async processing** for non-blocking operations

## 🧪 Testing Strategy

### Unit Testing
- **JUnit 5** for unit tests
- **Mockito** for mocking dependencies
- **TestContainers** for integration tests

### Integration Testing
- **End-to-end** order flow testing
- **Saga rollback** testing
- **Kafka event** testing with embedded Kafka

### Load Testing
- **JMeter** for API load testing
- **Kafka performance** testing
- **Redis performance** testing

## 🔍 Monitoring & Observability

### Health Checks
- **Spring Boot Actuator** endpoints
- **Service health** monitoring
- **Dependency health** checks

### Metrics
- **Micrometer** metrics collection
- **Prometheus** metrics endpoint
- **Custom business** metrics

### Logging
- **Structured logging** with SLF4J
- **Log correlation** across services
- **Centralized log** aggregation

## 🚨 Error Handling

### Global Exception Handling
- **@ControllerAdvice** for global error handling
- **Standardized error** responses
- **Error logging** and monitoring

### Retry Mechanisms
- **Kafka consumer** retry logic
- **Exponential backoff** for transient failures
- **Dead letter queues** for failed messages

### Circuit Breakers
- **Resilience4j** integration (planned)
- **Fallback mechanisms** for service failures
- **Graceful degradation** strategies

## 📋 API Documentation

### OpenAPI/Swagger
- **Auto-generated** API documentation
- **Interactive** API testing
- **Request/response** schemas

### API Endpoints
- **RESTful** design principles
- **HTTP status codes** for responses
- **Content negotiation** support

## 🔄 Event Schema

### OrderCreatedEvent
```json
{
  "eventId": "uuid",
  "eventType": "OrderCreatedEvent",
  "timestamp": "2024-01-01T10:00:00",
  "orderId": "order123",
  "customerId": "customer123",
  "items": [...],
  "totalAmount": 100.00
}
```

### InventoryReservedEvent
```json
{
  "eventId": "uuid",
  "eventType": "InventoryReservedEvent",
  "timestamp": "2024-01-01T10:00:01",
  "orderId": "order123",
  "productId": "product1",
  "quantity": 2,
  "success": true,
  "message": "Inventory reserved successfully"
}
```

## 🎯 Future Enhancements

### Planned Features
- **Distributed tracing** with Zipkin
- **Advanced monitoring** with Prometheus + Grafana
- **Message persistence** with Kafka Streams
- **Event sourcing** for audit trails
- **CQRS** pattern implementation
- **Multi-tenancy** support
- **API versioning** strategy

### Performance Improvements
- **Database optimization** with connection pooling
- **Caching strategies** with Redis clusters
- **Message compression** for Kafka
- **Async processing** improvements
- **Resource optimization** for containers 