# Event-Driven Microservices Platform - Complete Documentation

## 📚 Table of Contents

1. [Platform Overview](#platform-overview)
2. [Architecture Deep Dive](#architecture-deep-dive)
3. [Installation & Setup](#installation--setup)
4. [API Documentation](#api-documentation)
5. [Event Flow & Messaging](#event-flow--messaging)
6. [Service Configuration](#service-configuration)
7. [Monitoring & Observability](#monitoring--observability)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Development Guide](#development-guide)
10. [Production Deployment](#production-deployment)

## 🏗️ Platform Overview

### What is This Platform?
A complete event-driven microservices platform built with Java Spring Boot that demonstrates real-time transaction workflows using:
- **4 Microservices**: Order, Inventory, Payment, Notification
- **Event-Driven Architecture**: Apache Kafka for asynchronous communication
- **Distributed Transactions**: Saga Pattern for consistency
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Caching**: Redis for transient data
- **Containerization**: Docker & Docker Compose

### Key Features
- ✅ **Asynchronous Processing**: Non-blocking event-driven communication
- ✅ **Distributed Transactions**: Saga pattern with rollback capabilities
- ✅ **Service Discovery**: Automatic service registration and discovery
- ✅ **API Gateway**: Centralized routing and load balancing
- ✅ **Health Monitoring**: Built-in health checks and metrics
- ✅ **OpenAPI Documentation**: Auto-generated API documentation
- ✅ **Containerized**: Easy deployment with Docker
- ✅ **Scalable**: Horizontal scaling capabilities

## 🏛️ Architecture Deep Dive

### System Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  Eureka Server  │    │   Kafka + ZK    │
│   (Port: 8080)  │    │   (Port: 8761)  │    │   (Port: 9092)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Order Service  │    │ Inventory Svc   │    │  Payment Svc    │
│   (Port: 8081)  │    │  (Port: 8082)   │    │   (Port: 8083)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Notification Svc│    │     Redis       │    │   Monitoring    │
│   (Port: 8084)  │    │   (Port: 6379)  │    │   (Prometheus)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Service Responsibilities

#### 1. Order Service (`order-service`)
- **Purpose**: Manages order lifecycle and coordinates the transaction
- **Responsibilities**:
  - Create new orders
  - Store order data in Redis
  - Publish `OrderCreatedEvent` to Kafka
  - Handle order status updates
  - Provide order query endpoints

#### 2. Inventory Service (`inventory-service`)
- **Purpose**: Manages product inventory and stock reservations
- **Responsibilities**:
  - Reserve inventory for orders
  - Release reserved inventory on rollback
  - Track available vs reserved stock
  - Publish inventory events
  - Handle inventory queries

#### 3. Payment Service (`payment-service`)
- **Purpose**: Processes payments and handles payment failures
- **Responsibilities**:
  - Process payment transactions
  - Handle payment failures
  - Trigger rollback events
  - Publish payment events
  - Manage payment status

#### 4. Notification Service (`notification-service`)
- **Purpose**: Sends notifications for various events
- **Responsibilities**:
  - Send email/SMS notifications
  - Handle different notification types
  - Track notification status
  - Publish notification events

#### 5. API Gateway (`api-gateway`)
- **Purpose**: Single entry point for all client requests
- **Responsibilities**:
  - Route requests to appropriate services
  - Load balancing
  - CORS handling
  - Request/response transformation
  - Rate limiting (configurable)

#### 6. Eureka Server (`eureka-server`)
- **Purpose**: Service discovery and registration
- **Responsibilities**:
  - Register microservices
  - Service discovery
  - Health monitoring
  - Load balancing support

## 🚀 Installation & Setup

### Prerequisites
```bash
# Required Software
- Java 17+ (OpenJDK or Oracle JDK)
- Maven 3.8+
- Docker Desktop 4.0+
- Git
- At least 8GB RAM available
- 10GB free disk space
```

### Step 1: Clone and Setup
```bash
git clone <repository-url>
cd event-driven-platform
```

### Step 2: Build Services
**Windows:**
```cmd
build.bat
```

**Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

### Step 3: Start Platform
```bash
docker-compose up -d
```

### Step 4: Verify Installation
```bash
# Check all containers are running
docker-compose ps

# Check service health
curl http://localhost:8080/actuator/health
curl http://localhost:8761/actuator/health
```

## 📖 API Documentation

### Base URLs
- **API Gateway**: `http://localhost:8080`
- **Eureka Dashboard**: `http://localhost:8761`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Kafka UI**: `http://localhost:8085`

### Order Service APIs

#### 1. Create Order
```http
POST /orders
Content-Type: application/json

{
  "customerId": "customer123",
  "items": [
    {
      "productId": "product1",
      "quantity": 2,
      "unitPrice": 25.00
    }
  ],
  "totalAmount": 50.00,
  "shippingAddress": "123 Test St, Test City",
  "paymentMethod": "CREDIT_CARD"
}
```

**Response:**
```json
{
  "orderId": "order-12345",
  "customerId": "customer123",
  "status": "CREATED",
  "totalAmount": 50.00,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

#### 2. Get Order by ID
```http
GET /orders/{orderId}
```

#### 3. Get All Orders
```http
GET /orders
```

#### 4. Get Orders by Customer
```http
GET /orders/customer/{customerId}
```

### Inventory Service APIs

#### 1. Get Product Inventory
```http
GET /inventory/{productId}
```

#### 2. Reserve Inventory
```http
POST /inventory/reserve
Content-Type: application/json

{
  "productId": "product1",
  "quantity": 2
}
```

### Payment Service APIs

#### 1. Process Payment
```http
POST /payment/process
Content-Type: application/json

{
  "orderId": "order-12345",
  "amount": 50.00,
  "paymentMethod": "CREDIT_CARD",
  "cardNumber": "****-****-****-1234"
}
```

## 🔄 Event Flow & Messaging

### Kafka Topics
- `order-events`: Order-related events
- `inventory-events`: Inventory-related events
- `payment-events`: Payment-related events
- `notification-events`: Notification events

### Event Types

#### 1. OrderCreatedEvent
```json
{
  "eventId": "evt-12345",
  "eventType": "ORDER_CREATED",
  "timestamp": "2024-01-15T10:30:00Z",
  "orderId": "order-12345",
  "customerId": "customer123",
  "items": [...],
  "totalAmount": 50.00
}
```

#### 2. InventoryReservedEvent
```json
{
  "eventId": "evt-12346",
  "eventType": "INVENTORY_RESERVED",
  "timestamp": "2024-01-15T10:30:05Z",
  "orderId": "order-12345",
  "productId": "product1",
  "quantity": 2,
  "reserved": true
}
```

#### 3. PaymentProcessedEvent
```json
{
  "eventId": "evt-12347",
  "eventType": "PAYMENT_PROCESSED",
  "timestamp": "2024-01-15T10:30:10Z",
  "orderId": "order-12345",
  "amount": 50.00,
  "status": "SUCCESS"
}
```

### Saga Pattern Flow

#### Success Flow
1. **Order Creation**: Client → Order Service → `OrderCreatedEvent`
2. **Inventory Reservation**: Inventory Service → `InventoryReservedEvent`
3. **Payment Processing**: Payment Service → `PaymentProcessedEvent`
4. **Notification**: Notification Service → Success notification

#### Rollback Flow (Payment Failure)
1. **Payment Failure**: Payment Service → `PaymentFailedEvent`
2. **Inventory Rollback**: Inventory Service → Release reserved stock
3. **Order Update**: Order Service → Update order status to FAILED
4. **Notification**: Notification Service → Failure notification

## ⚙️ Service Configuration

### Environment Variables

#### Order Service
```yaml
spring:
  application:
    name: order-service
  kafka:
    bootstrap-servers: kafka:9092
  redis:
    host: redis
    port: 6379
  cloud:
    discovery:
      client:
        service-url:
          defaultZone: http://eureka-server:8761/eureka/
```

#### Inventory Service
```yaml
spring:
  application:
    name: inventory-service
  kafka:
    bootstrap-servers: kafka:9092
  redis:
    host: redis
    port: 6379
```

### Kafka Configuration
```yaml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: ${spring.application.name}
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

## 📊 Monitoring & Observability

### Health Endpoints
```bash
# Service Health Checks
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Order Service
curl http://localhost:8082/actuator/health  # Inventory Service
curl http://localhost:8083/actuator/health  # Payment Service
curl http://localhost:8084/actuator/health  # Notification Service
curl http://localhost:8761/actuator/health  # Eureka Server
```

### Metrics Endpoints
```bash
# Prometheus Metrics
curl http://localhost:8080/actuator/prometheus  # API Gateway
curl http://localhost:8081/actuator/prometheus  # Order Service
```

### Logging
```bash
# View all logs
docker-compose logs

# Follow specific service logs
docker-compose logs -f order-service

# Search for errors
docker-compose logs | grep ERROR
```

## 🔧 Troubleshooting Guide

### Common Issues

#### 1. Services Not Starting
```bash
# Check Docker resources
docker system df
docker stats

# Restart Docker Desktop (Windows/Mac)
# Restart Docker service (Linux)
sudo systemctl restart docker
```

#### 2. Kafka Connection Issues
```bash
# Check Kafka container
docker-compose logs kafka

# Verify Kafka is ready
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### 3. Redis Connection Issues
```bash
# Check Redis container
docker-compose logs redis

# Test Redis connection
docker exec -it redis redis-cli ping
```

#### 4. Port Conflicts
```bash
# Check port usage
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Kill process if needed
taskkill /PID <PID> /F        # Windows
kill -9 <PID>                 # Linux/Mac
```

### Debug Commands
```bash
# Check container status
docker-compose ps

# View service logs
docker-compose logs -f <service-name>

# Access container shell
docker exec -it <container-name> /bin/bash

# Check network connectivity
docker network ls
docker network inspect event-driven_event-driven-network
```

## 👨‍💻 Development Guide

### Adding New Services
1. Create new service directory
2. Add Maven POM with dependencies
3. Create Spring Boot application class
4. Add service to `docker-compose.yml`
5. Update parent `pom.xml` modules list

### Adding New Events
1. Create event class in `shared-lib`
2. Add to appropriate Kafka topic
3. Create event listeners in services
4. Update event flow documentation

### Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Test specific service
cd order-service && mvn test
```

## 🚀 Production Deployment

### Production Considerations
- **Security**: Add authentication/authorization
- **SSL/TLS**: Enable HTTPS
- **Load Balancing**: Use external load balancer
- **Monitoring**: Add APM tools (New Relic, DataDog)
- **Logging**: Centralized logging (ELK Stack)
- **Backup**: Database and configuration backups
- **Scaling**: Horizontal scaling with Kubernetes

### Kubernetes Deployment
```yaml
# Example Kubernetes deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: order-service
        image: order-service:latest
        ports:
        - containerPort: 8081
```

### Environment-Specific Configs
```yaml
# application-prod.yml
spring:
  profiles: prod
  kafka:
    bootstrap-servers: kafka-cluster:9092
  redis:
    host: redis-cluster
    password: ${REDIS_PASSWORD}
```

## 📞 Support & Resources

### Documentation Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Redis Documentation](https://redis.io/documentation)

### Community Resources
- [Spring Community](https://spring.io/community)
- [Kafka Community](https://kafka.apache.org/community)
- [Docker Community](https://www.docker.com/community)

### Getting Help
1. Check service logs for errors
2. Verify all prerequisites are installed
3. Review configuration files
4. Check network connectivity
5. Consult troubleshooting guide above

---

**Last Updated**: January 2024  
**Version**: 1.0.0  
**Maintainer**: Event-Driven Platform Team 