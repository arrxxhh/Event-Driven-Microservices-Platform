# Event-Driven Microservices Platform - Usage Guide

## 📚 Table of Contents

1. [Quick Start](#quick-start)
2. [Platform Setup](#platform-setup)
3. [Service Interaction](#service-interaction)
4. [Event Flow Testing](#event-flow-testing)
5. [Monitoring & Debugging](#monitoring--debugging)
6. [Common Operations](#common-operations)
7. [Troubleshooting](#troubleshooting)
8. [Advanced Usage](#advanced-usage)

## 🚀 Quick Start

### Prerequisites Check

Before starting, ensure you have the following installed:

```bash
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.8+)
mvn -version

# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version
```

### Windows-Specific Setup

If you're on Windows and have multiple Java versions:

```cmd
# Set Java 17 for this session
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

# Verify Java version
java -version
```

### One-Command Setup

**Windows:**
```cmd
# Build and start everything
build.bat && docker-compose up -d
```

**Linux/Mac:**
```bash
# Build and start everything
chmod +x build.sh && ./build.sh && docker-compose up -d
```

## 🏗️ Platform Setup

### Step 1: Build Services

**Option A: Using Build Scripts**

Windows:
```cmd
build.bat
```

Linux/Mac:
```bash
chmod +x build.sh
./build.sh
```

**Option B: Manual Build**
```bash
# Build shared library first
cd shared-lib && mvn clean install

# Build all services
cd ../order-service && mvn clean package
cd ../inventory-service && mvn clean package
cd ../payment-service && mvn clean package
cd ../notification-service && mvn clean package
cd ../eureka-server && mvn clean package
cd ../api-gateway && mvn clean package
```

### Step 2: Start Infrastructure

```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps
```

### Step 3: Verify Setup

```bash
# Check all services are running
docker-compose ps

# Check service health
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8761/actuator/health  # Eureka Server
curl http://localhost:8081/actuator/health  # Order Service
curl http://localhost:8082/actuator/health  # Inventory Service
curl http://localhost:8083/actuator/health  # Payment Service
curl http://localhost:8084/actuator/health  # Notification Service
```

### Step 4: Access Dashboards

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Kafka UI**: http://localhost:8085

## 🔄 Service Interaction

### Creating an Order

**Using cURL:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

**Using PowerShell (Windows):**
```powershell
$body = @{
    customerId = "customer123"
    items = @(
        @{
            productId = "product1"
            quantity = 2
            unitPrice = 25.00
        }
    )
    totalAmount = 50.00
    shippingAddress = "123 Test St, Test City"
    paymentMethod = "CREDIT_CARD"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/orders" -Method POST -Body $body -ContentType "application/json"
```

**Expected Response:**
```json
{
  "orderId": "order-12345",
  "customerId": "customer123",
  "status": "CREATED",
  "totalAmount": 50.00,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### Querying Orders

```bash
# Get order by ID
curl http://localhost:8080/orders/order-12345

# Get all orders
curl http://localhost:8080/orders

# Get orders by customer
curl http://localhost:8080/orders/customer/customer123
```

### Checking Inventory

```bash
# Get product inventory
curl http://localhost:8080/inventory/product1

# Reserve inventory
curl -X POST http://localhost:8080/inventory/reserve \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product1",
    "quantity": 2
  }'
```

### Processing Payments

```bash
# Process payment
curl -X POST http://localhost:8080/payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-12345",
    "amount": 50.00,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "****-****-****-1234"
  }'
```

## 🔄 Event Flow Testing

### Testing Successful Flow

1. **Create an Order:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer123",
    "items": [{"productId": "product1", "quantity": 1, "unitPrice": 25.00}],
    "totalAmount": 25.00,
    "shippingAddress": "123 Test St",
    "paymentMethod": "CREDIT_CARD"
  }'
```

2. **Monitor Events in Kafka:**
```bash
# Check Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor order events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning

# Monitor inventory events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic inventory-events \
  --from-beginning
```

3. **Check Service Logs:**
```bash
# Follow order service logs
docker-compose logs -f order-service

# Follow inventory service logs
docker-compose logs -f inventory-service

# Follow payment service logs
docker-compose logs -f payment-service
```

### Testing Saga Rollback

1. **Create an Order with Insufficient Inventory:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer123",
    "items": [{"productId": "product1", "quantity": 1000, "unitPrice": 25.00}],
    "totalAmount": 25000.00,
    "shippingAddress": "123 Test St",
    "paymentMethod": "CREDIT_CARD"
  }'
```

2. **Monitor Rollback Events:**
```bash
# Check for rollback events
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic inventory-events \
  --from-beginning
```

## 📊 Monitoring & Debugging

### Health Checks

```bash
# Check all service health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8761/actuator/health
```

### Metrics

```bash
# Get Prometheus metrics
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8081/actuator/prometheus
```

### Logs

```bash
# View all logs
docker-compose logs

# Follow specific service logs
docker-compose logs -f order-service
docker-compose logs -f inventory-service
docker-compose logs -f payment-service

# Search for errors
docker-compose logs | grep ERROR
docker-compose logs | grep WARN

# Get logs for specific time
docker-compose logs --since="2024-01-15T10:00:00" order-service
```

### Kafka Monitoring

```bash
# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe topic
docker exec -it kafka kafka-topics --describe --topic order-events --bootstrap-server localhost:9092

# Check consumer groups
docker exec -it kafka kafka-consumer-groups --list --bootstrap-server localhost:9092

# Check consumer lag
docker exec -it kafka kafka-consumer-groups --describe --group inventory-service --bootstrap-server localhost:9092
```

### Redis Monitoring

```bash
# Connect to Redis CLI
docker exec -it redis redis-cli

# Check Redis info
docker exec -it redis redis-cli info

# List all keys
docker exec -it redis redis-cli keys "*"

# Get specific key
docker exec -it redis redis-cli get "order:order-12345"
```

## 🔧 Common Operations

### Restarting Services

```bash
# Restart specific service
docker-compose restart order-service

# Restart all services
docker-compose restart

# Restart with rebuild
docker-compose down
docker-compose up -d --build
```

### Scaling Services

```bash
# Scale order service to 3 instances
docker-compose up -d --scale order-service=3

# Scale inventory service to 2 instances
docker-compose up -d --scale inventory-service=2
```

### Updating Configuration

```bash
# Edit service configuration
docker-compose down
# Edit application.yml files
docker-compose up -d
```

### Database Operations

```bash
# Clear Redis data
docker exec -it redis redis-cli flushall

# Backup Redis data
docker exec -it redis redis-cli save

# Check Redis memory usage
docker exec -it redis redis-cli info memory
```

### Kafka Operations

```bash
# Create new topic
docker exec -it kafka kafka-topics --create \
  --topic test-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Delete topic
docker exec -it kafka kafka-topics --delete \
  --topic test-events \
  --bootstrap-server localhost:9092

# Produce test message
docker exec -it kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic test-events
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. Services Not Starting

**Check Docker resources:**
```bash
docker system df
docker stats
```

**Check service logs:**
```bash
docker-compose logs order-service
```

**Restart Docker Desktop (Windows/Mac) or Docker service (Linux):**
```bash
# Linux
sudo systemctl restart docker
```

#### 2. Port Conflicts

**Check port usage:**
```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

**Kill process if needed:**
```bash
# Windows
taskkill /PID <PID> /F

# Linux/Mac
kill -9 <PID>
```

#### 3. Kafka Connection Issues

**Check Kafka container:**
```bash
docker-compose logs kafka
```

**Verify Kafka is ready:**
```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### 4. Redis Connection Issues

**Check Redis container:**
```bash
docker-compose logs redis
```

**Test Redis connection:**
```bash
docker exec -it redis redis-cli ping
```

### Debug Commands

```bash
# Check container status
docker-compose ps

# Check network connectivity
docker network ls
docker network inspect event-driven_event-driven-network

# Access container shell
docker exec -it order-service /bin/bash

# Check service discovery
curl http://localhost:8761/eureka/apps
```

### Performance Issues

**Check resource usage:**
```bash
docker stats
docker system df
```

**Monitor Kafka performance:**
```bash
docker exec -it kafka kafka-consumer-groups --describe --all-groups --bootstrap-server localhost:9092
```

**Check Redis performance:**
```bash
docker exec -it redis redis-cli info stats
docker exec -it redis redis-cli info memory
```

## 🚀 Advanced Usage

### Custom Event Types

1. **Create new event in shared-lib:**
```java
public class CustomEvent extends BaseEvent {
    private String customField;
    // getters, setters, constructors
}
```

2. **Add to Kafka configuration:**
```java
@Bean
public NewTopic customTopic() {
    return TopicBuilder.name("custom-events")
        .partitions(3)
        .replicas(1)
        .build();
}
```

3. **Create producer:**
```java
kafkaTemplate.send("custom-events", new CustomEvent());
```

4. **Create consumer:**
```java
@KafkaListener(topics = "custom-events", groupId = "my-service")
public void handleCustomEvent(CustomEvent event) {
    // Process event
}
```

### Adding New Services

1. **Create service directory:**
```bash
mkdir new-service
cd new-service
```

2. **Create Maven POM:**
```xml
<parent>
    <groupId>com.eventdriven</groupId>
    <artifactId>event-driven-platform</artifactId>
    <version>1.0.0</version>
</parent>

<artifactId>new-service</artifactId>
<dependencies>
    <dependency>
        <groupId>com.eventdriven</groupId>
        <artifactId>shared-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```

3. **Add to docker-compose.yml:**
```yaml
new-service:
  build: ./new-service
  ports:
    - "8085:8085"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
  depends_on:
    - kafka
    - redis
    - eureka-server
```

4. **Update parent pom.xml:**
```xml
<modules>
    <!-- existing modules -->
    <module>new-service</module>
</modules>
```

### Custom Configuration

**Environment-specific configs:**
```yaml
# application-dev.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
  redis:
    host: localhost

# application-prod.yml
spring:
  kafka:
    bootstrap-servers: kafka-cluster:9092
  redis:
    host: redis-cluster
    password: ${REDIS_PASSWORD}
```

**Run with specific profile:**
```bash
docker-compose run -e SPRING_PROFILES_ACTIVE=prod order-service
```

### Integration Testing

**Using test scripts:**
```bash
# Run integration tests
chmod +x test-platform.sh
./test-platform.sh
```

**Manual testing:**
```bash
# Test complete flow
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"customerId":"test","items":[{"productId":"product1","quantity":1,"unitPrice":10.00}],"totalAmount":10.00,"shippingAddress":"test","paymentMethod":"CREDIT_CARD"}'

# Verify order was created
curl http://localhost:8080/orders

# Check inventory was updated
curl http://localhost:8080/inventory/product1
```

---

## 📞 Support

### Getting Help

1. **Check logs first:**
```bash
docker-compose logs
```

2. **Verify prerequisites:**
```bash
java -version
mvn -version
docker --version
```

3. **Check service health:**
```bash
curl http://localhost:8080/actuator/health
```

4. **Review configuration files:**
- `application.yml` files in each service
- `docker-compose.yml`
- Maven `pom.xml` files

### Useful Commands Reference

```bash
# Start platform
docker-compose up -d

# Stop platform
docker-compose down

# View logs
docker-compose logs -f

# Restart services
docker-compose restart

# Scale services
docker-compose up -d --scale order-service=3

# Check status
docker-compose ps

# Access service shell
docker exec -it order-service /bin/bash

# Monitor Kafka
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor Redis
docker exec -it redis redis-cli
```

---

**Happy Coding! 🚀** 