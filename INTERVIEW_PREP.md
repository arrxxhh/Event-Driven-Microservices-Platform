# Event-Driven Microservices - Interview Preparation Guide

## 📚 Table of Contents

1. [Core Concepts](#core-concepts)
2. [Microservices Architecture](#microservices-architecture)
3. [Event-Driven Architecture](#event-driven-architecture)
4. [Apache Kafka Deep Dive](#apache-kafka-deep-dive)
5. [Saga Pattern](#saga-pattern)
6. [Spring Boot & Spring Cloud](#spring-boot--spring-cloud)
7. [Distributed Systems](#distributed-systems)
8. [Common Interview Questions](#common-interview-questions)
9. [Practical Implementation](#practical-implementation)
10. [System Design Questions](#system-design-questions)

## 🎯 Core Concepts

### What are Microservices?

**Definition**: Microservices is an architectural style where an application is built as a collection of small, independent services that communicate over well-defined APIs.

**Key Characteristics**:
- **Single Responsibility**: Each service has one specific business capability
- **Independent Deployment**: Services can be deployed independently
- **Technology Diversity**: Different services can use different technologies
- **Data Isolation**: Each service manages its own database
- **Fault Isolation**: Failure in one service doesn't bring down the entire system

**Example from Our Platform**:
```java
// Order Service - handles order management
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        // Business logic for order creation
        Order order = new Order();
        // Save to Redis
        // Publish event to Kafka
        return order;
    }
}

// Inventory Service - handles inventory management
@Service
public class InventoryService {
    @KafkaListener(topics = "order-events")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Reserve inventory
        // Publish inventory event
    }
}
```

### Monolithic vs Microservices

| Aspect | Monolithic | Microservices |
|--------|------------|---------------|
| **Deployment** | Single unit | Independent services |
| **Scaling** | Scale entire app | Scale individual services |
| **Technology** | Single tech stack | Multiple tech stacks |
| **Database** | Single database | Database per service |
| **Fault Tolerance** | Single point of failure | Isolated failures |
| **Development** | Large team coordination | Small team autonomy |

## 🏗️ Microservices Architecture

### Service Discovery

**Problem**: How do services find each other in a distributed environment?

**Solution**: Service Discovery using Netflix Eureka

```java
// Eureka Server Configuration
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

// Service Client Configuration
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {
    // Service automatically registers with Eureka
}
```

**Benefits**:
- Dynamic service registration
- Load balancing
- Health monitoring
- Automatic failover

### API Gateway

**Purpose**: Single entry point for all client requests

```yaml
# Spring Cloud Gateway Configuration
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/orders/**
        - id: inventory-service
          uri: lb://inventory-service
          predicates:
            - Path=/inventory/**
```

**Responsibilities**:
- **Routing**: Direct requests to appropriate services
- **Load Balancing**: Distribute load across service instances
- **Authentication**: Centralized security
- **Rate Limiting**: Control request rates
- **CORS**: Handle cross-origin requests

### Circuit Breaker Pattern

**Problem**: How to handle service failures gracefully?

```java
@CircuitBreaker(name = "paymentService")
public PaymentResponse processPayment(PaymentRequest request) {
    return paymentService.process(request);
}

@FallbackMethod("fallbackPayment")
public PaymentResponse fallbackPayment(PaymentRequest request, Exception ex) {
    // Return cached response or default behavior
    return PaymentResponse.builder()
        .status("PENDING")
        .message("Payment processing delayed")
        .build();
}
```

## 🔄 Event-Driven Architecture

### What is Event-Driven Architecture?

**Definition**: A software architecture pattern that promotes the production, detection, consumption, and reaction to events.

**Key Components**:
1. **Event Producers**: Services that generate events
2. **Event Consumers**: Services that react to events
3. **Event Bus**: Message broker (Kafka in our case)
4. **Event Store**: Persistent storage of events

### Event Types

#### 1. Domain Events
```java
public class OrderCreatedEvent extends BaseEvent {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    
    // Represents a business event that occurred
}
```

#### 2. Integration Events
```java
public class InventoryReservedEvent extends BaseEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private boolean reserved;
    
    // Used for inter-service communication
}
```

### Event Sourcing vs Event-Driven

**Event Sourcing**:
- Stores all events that happened to an entity
- Rebuilds state by replaying events
- Provides audit trail and temporal queries

**Event-Driven**:
- Uses events for communication
- Services react to events
- Loose coupling between services

## 📊 Apache Kafka Deep Dive

### What is Apache Kafka?

**Definition**: A distributed streaming platform that can be used for building real-time data pipelines and streaming applications.

### Core Concepts

#### 1. Topics and Partitions
```bash
# Create a topic with 3 partitions
kafka-topics --create --topic order-events \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1
```

**Partitioning Strategy**:
```java
// Order events partitioned by customerId
@KafkaListener(topics = "order-events", groupId = "inventory-service")
public void handleOrderCreated(OrderCreatedEvent event) {
    // Process order creation
}
```

#### 2. Producers and Consumers
```java
// Producer Configuration
@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
}

// Consumer Configuration
@KafkaListener(topics = "order-events", groupId = "inventory-service")
public void handleOrderCreated(OrderCreatedEvent event) {
    log.info("Received order created event: {}", event.getOrderId());
    inventoryService.reserveInventory(event);
}
```

#### 3. Consumer Groups
```java
// Multiple instances of the same service form a consumer group
@KafkaListener(topics = "order-events", groupId = "inventory-service")
public void handleOrderCreated(OrderCreatedEvent event) {
    // Only one instance in the group processes each message
}
```

### Kafka vs Other Message Brokers

| Feature | Kafka | RabbitMQ | ActiveMQ |
|---------|-------|----------|----------|
| **Persistence** | Disk-based | Memory/Disk | Memory/Disk |
| **Ordering** | Per partition | Per queue | Per queue |
| **Scaling** | Horizontal | Vertical | Vertical |
| **Throughput** | Very high | High | Medium |
| **Latency** | Low | Very low | Low |

## 🔄 Saga Pattern

### What is the Saga Pattern?

**Definition**: A sequence of local transactions where each transaction updates data within a single service and publishes an event to trigger the next transaction.

### Saga Types

#### 1. Choreography-Based Saga
```java
// Order Service
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        orderRepository.save(order);
        
        // Publish event - other services react
        kafkaTemplate.send("order-events", new OrderCreatedEvent(order));
        return order;
    }
}

// Inventory Service
@Service
public class InventoryService {
    @KafkaListener(topics = "order-events")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            reserveInventory(event);
            kafkaTemplate.send("inventory-events", new InventoryReservedEvent(event));
        } catch (Exception e) {
            // Publish compensation event
            kafkaTemplate.send("inventory-events", new InventoryRollbackEvent(event));
        }
    }
}
```

#### 2. Orchestration-Based Saga
```java
// Saga Orchestrator
@Service
public class OrderSagaOrchestrator {
    public void processOrder(OrderRequest request) {
        try {
            // Step 1: Create Order
            Order order = orderService.createOrder(request);
            
            // Step 2: Reserve Inventory
            inventoryService.reserveInventory(order);
            
            // Step 3: Process Payment
            paymentService.processPayment(order);
            
            // Step 4: Send Notification
            notificationService.sendNotification(order);
            
        } catch (Exception e) {
            // Compensate for completed steps
            compensateOrder(order);
        }
    }
}
```

### Compensation Logic
```java
@Service
public class InventoryService {
    @KafkaListener(topics = "payment-events")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // Compensate: Release reserved inventory
        releaseReservedInventory(event.getOrderId());
    }
    
    private void releaseReservedInventory(String orderId) {
        // Business logic to release inventory
        log.info("Releasing reserved inventory for order: {}", orderId);
    }
}
```

## 🌱 Spring Boot & Spring Cloud

### Spring Boot Auto-Configuration

**How it works**:
```java
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

**Auto-configuration classes**:
- `KafkaAutoConfiguration`: Sets up Kafka producers/consumers
- `RedisAutoConfiguration`: Configures Redis connection
- `WebMvcAutoConfiguration`: Sets up web endpoints
- `ActuatorAutoConfiguration`: Enables monitoring endpoints

### Spring Cloud Components

#### 1. Service Discovery (Eureka)
```java
@EnableDiscoveryClient
@SpringBootApplication
public class OrderServiceApplication {
    // Automatically registers with Eureka
}
```

#### 2. Circuit Breaker (Resilience4j)
```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
public PaymentResponse processPayment(PaymentRequest request) {
    return paymentService.process(request);
}
```

#### 3. Distributed Tracing (Sleuth)
```java
// Automatically adds trace IDs to logs
@Slf4j
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        // Trace ID automatically included in logs
    }
}
```

## 🌐 Distributed Systems

### CAP Theorem

**Definition**: In a distributed system, you can only guarantee two out of three properties:
- **Consistency (C)**: All nodes see the same data
- **Availability (A)**: System remains operational
- **Partition Tolerance (P)**: System continues despite network partitions

**Our Platform's Approach**:
- **Consistency**: Eventual consistency through events
- **Availability**: High availability through service replication
- **Partition Tolerance**: Handled by Kafka's distributed nature

### Eventual Consistency

```java
// Order Service - Immediate consistency
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        orderRepository.save(order); // Immediate consistency
        
        // Eventual consistency for other services
        kafkaTemplate.send("order-events", new OrderCreatedEvent(order));
        return order;
    }
}

// Inventory Service - Eventual consistency
@Service
public class InventoryService {
    @KafkaListener(topics = "order-events")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // May receive event after some delay
        // But will eventually process it
    }
}
```

### Distributed Transactions

**Problem**: How to maintain consistency across multiple services?

**Solutions**:
1. **Saga Pattern** (What we use)
2. **Two-Phase Commit (2PC)**
3. **Event Sourcing**
4. **CQRS (Command Query Responsibility Segregation)**

## ❓ Common Interview Questions

### 1. Why Microservices?

**Answer**: Microservices provide:
- **Scalability**: Scale individual services based on demand
- **Fault Isolation**: Failure in one service doesn't affect others
- **Technology Diversity**: Use different technologies for different services
- **Team Autonomy**: Small teams can work independently
- **Deployment Flexibility**: Deploy services independently

### 2. How do you handle distributed transactions?

**Answer**: We use the Saga pattern:
- Break down transactions into local transactions
- Use events to coordinate between services
- Implement compensation logic for rollbacks
- Example: Order → Inventory → Payment → Notification

### 3. What are the challenges of microservices?

**Answer**:
- **Network Latency**: Inter-service communication overhead
- **Data Consistency**: Maintaining consistency across services
- **Service Discovery**: Finding and connecting to services
- **Monitoring**: Observing distributed system behavior
- **Testing**: Testing interactions between services

### 4. How do you ensure message ordering in Kafka?

**Answer**:
- Use the same partition key for related messages
- Kafka guarantees ordering within a partition
- Example: All events for the same order use orderId as partition key

### 5. What is eventual consistency?

**Answer**: A consistency model where:
- Updates are propagated asynchronously
- Different nodes may have different data temporarily
- All nodes will eventually have the same data
- Example: Order created immediately, inventory updated later

### 6. How do you handle service failures?

**Answer**:
- **Circuit Breaker**: Prevent cascading failures
- **Retry Logic**: Retry failed operations with backoff
- **Fallback Methods**: Provide alternative responses
- **Health Checks**: Monitor service health
- **Graceful Degradation**: Continue with reduced functionality

### 7. What is the difference between synchronous and asynchronous communication?

**Answer**:
- **Synchronous**: Caller waits for response (REST APIs)
- **Asynchronous**: Caller doesn't wait (Event-driven)
- **Benefits of Async**: Better scalability, fault tolerance, loose coupling

### 8. How do you monitor microservices?

**Answer**:
- **Health Checks**: `/actuator/health` endpoints
- **Metrics**: Prometheus metrics via Micrometer
- **Distributed Tracing**: Zipkin for request tracing
- **Centralized Logging**: ELK Stack
- **Service Discovery**: Eureka dashboard

## 🛠️ Practical Implementation

### Code Examples

#### 1. Event Producer
```java
@Service
@Slf4j
public class OrderService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        orderRepository.save(order);
        
        // Publish event
        OrderCreatedEvent event = new OrderCreatedEvent(order);
        kafkaTemplate.send("order-events", order.getOrderId(), event)
            .addCallback(
                result -> log.info("Event sent successfully"),
                ex -> log.error("Failed to send event", ex)
            );
        
        return order;
    }
}
```

#### 2. Event Consumer
```java
@Service
@Slf4j
public class InventoryService {
    @KafkaListener(topics = "order-events", groupId = "inventory-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Processing order created event: {}", event.getOrderId());
            reserveInventory(event);
            
            // Publish success event
            InventoryReservedEvent successEvent = new InventoryReservedEvent(event);
            kafkaTemplate.send("inventory-events", successEvent);
            
        } catch (Exception e) {
            log.error("Failed to reserve inventory", e);
            // Publish failure event
            InventoryRollbackEvent failureEvent = new InventoryRollbackEvent(event);
            kafkaTemplate.send("inventory-events", failureEvent);
        }
    }
}
```

#### 3. Circuit Breaker Implementation
```java
@Service
public class PaymentService {
    @CircuitBreaker(name = "paymentProcessor", fallbackMethod = "fallbackPayment")
    public PaymentResponse processPayment(PaymentRequest request) {
        // Simulate external payment processing
        if (Math.random() < 0.1) { // 10% failure rate
            throw new RuntimeException("Payment processing failed");
        }
        return PaymentResponse.builder()
            .status("SUCCESS")
            .transactionId(UUID.randomUUID().toString())
            .build();
    }
    
    public PaymentResponse fallbackPayment(PaymentRequest request, Exception ex) {
        log.warn("Payment processing failed, using fallback: {}", ex.getMessage());
        return PaymentResponse.builder()
            .status("PENDING")
            .message("Payment will be processed later")
            .build();
    }
}
```

## 🏗️ System Design Questions

### 1. Design an E-commerce Order Processing System

**Requirements**:
- Process orders with inventory checks
- Handle payment processing
- Send notifications
- Handle failures gracefully

**Solution**:
```
Client → API Gateway → Order Service → Kafka → Inventory Service
                                    ↓
                              Payment Service
                                    ↓
                              Notification Service
```

**Key Components**:
- **API Gateway**: Single entry point
- **Order Service**: Order management
- **Inventory Service**: Stock management
- **Payment Service**: Payment processing
- **Notification Service**: User notifications
- **Kafka**: Event bus
- **Redis**: Caching
- **Eureka**: Service discovery

### 2. Design a Real-time Notification System

**Requirements**:
- Send notifications to millions of users
- Support multiple notification types
- Handle high throughput
- Ensure delivery

**Solution**:
```
Event Source → Kafka → Notification Service → Message Queue → Delivery Service
```

**Key Components**:
- **Kafka**: Event streaming
- **Notification Service**: Business logic
- **Message Queue**: Reliable delivery
- **Delivery Service**: Actual sending
- **Database**: Notification history

### 3. Design a Distributed Cache System

**Requirements**:
- Cache data across multiple services
- Handle cache invalidation
- High availability
- Consistency

**Solution**:
```
Service → Redis Cluster → Cache Manager → Event Bus
```

**Key Components**:
- **Redis Cluster**: Distributed caching
- **Cache Manager**: Cache coordination
- **Event Bus**: Cache invalidation events
- **Health Checks**: Cache health monitoring

## 📚 Additional Resources

### Books
- "Building Microservices" by Sam Newman
- "Event-Driven Architecture" by Hugh Taylor
- "Designing Data-Intensive Applications" by Martin Kleppmann
- "Kafka: The Definitive Guide" by Neha Narkhede

### Online Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Microservices.io](https://microservices.io/)
- [Martin Fowler's Blog](https://martinfowler.com/)

### Practice Platforms
- [HackerRank](https://www.hackerrank.com/)
- [LeetCode](https://leetcode.com/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)

---

**Remember**: 
- Understand the "why" behind every design decision
- Be prepared to discuss trade-offs
- Have real-world examples ready
- Practice coding the core concepts
- Stay updated with latest trends and technologies

**Good Luck with Your Interviews! 🚀** 