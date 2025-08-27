# Quick Start Guide

Get your Event-Driven Microservices Platform up and running in minutes!

## 🚀 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** - [Download here](https://adoptium.net/)
- **Maven 3.8+** - [Download here](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** - [Download here](https://www.docker.com/products/docker-desktop)
- **Git** - [Download here](https://git-scm.com/downloads)

## 📦 Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd event-driven-platform
```

### 2. Build All Services

**On Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

**On Windows:**
```cmd
build.bat
```

### 3. Start the Platform
```bash
docker-compose up -d
```

### 4. Verify Services
```bash
# Check if all containers are running
docker-compose ps

# Check service logs
docker-compose logs -f
```

## 🧪 Testing the Platform

### 1. Health Checks
Verify all services are healthy:

```bash
# Eureka Server
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health

# Order Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8082/actuator/health

# Payment Service
curl http://localhost:8083/actuator/health

# Notification Service
curl http://localhost:8084/actuator/health
```

### 2. Create a Test Order
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

### 3. Check Order Status
```bash
# Replace ORDER_ID with the ID from the previous response
curl http://localhost:8080/orders/ORDER_ID
```

### 4. View All Orders
```bash
curl http://localhost:8080/orders
```

## 🌐 Access Points

Once the platform is running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | Main entry point for all APIs |
| **Eureka Dashboard** | http://localhost:8761 | Service discovery and monitoring |
| **Kafka UI** | http://localhost:8085 | Kafka topic monitoring |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | API documentation |

## 📊 Monitoring

### Eureka Dashboard
- Visit http://localhost:8761
- View all registered services
- Check service health status
- Monitor service instances

### Kafka UI
- Visit http://localhost:8085
- Browse Kafka topics
- View message contents
- Monitor consumer groups

### Service Health
Each service exposes health endpoints:
- `/actuator/health` - Service health
- `/actuator/metrics` - Service metrics
- `/actuator/info` - Service information

## 🔧 Configuration

### Environment Variables
Key configuration options in `docker-compose.yml`:

```yaml
# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092

# Redis Configuration
SPRING_DATA_REDIS_HOST: redis

# Eureka Configuration
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
```

### Custom Configuration
To modify service configurations:

1. Edit the respective `application.yml` files
2. Rebuild the services: `./build.sh`
3. Restart containers: `docker-compose up -d`

## 🐛 Troubleshooting

### Common Issues

#### 1. Services Not Starting
```bash
# Check container logs
docker-compose logs [service-name]

# Check if ports are available
netstat -an | grep :8080
```

#### 2. Kafka Connection Issues
```bash
# Check Kafka status
docker-compose logs kafka

# Verify Kafka topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### 3. Redis Connection Issues
```bash
# Check Redis status
docker-compose logs redis

# Test Redis connection
docker exec -it redis redis-cli ping
```

#### 4. Service Discovery Issues
```bash
# Check Eureka status
curl http://localhost:8761/actuator/health

# Verify service registration
curl http://localhost:8761/eureka/apps
```

### Log Analysis
```bash
# View all logs
docker-compose logs

# Follow specific service logs
docker-compose logs -f order-service

# Search logs for errors
docker-compose logs | grep ERROR
```

## 🧹 Cleanup

### Stop Services
```bash
docker-compose down
```

### Remove All Data
```bash
docker-compose down -v
docker system prune -f
```

### Rebuild Everything
```bash
# Clean and rebuild
./build.sh
docker-compose up -d --build
```

## 📚 Next Steps

### Explore the Codebase
1. **Shared Library** (`shared-lib/`) - Common DTOs and configurations
2. **Order Service** (`order-service/`) - Order management logic
3. **Inventory Service** (`inventory-service/`) - Inventory management
4. **Payment Service** (`payment-service/`) - Payment processing
5. **Notification Service** (`notification-service/`) - Notifications

### Learn the Architecture
- Read [ARCHITECTURE.md](ARCHITECTURE.md) for detailed architecture
- Understand the event-driven flow
- Explore the saga pattern implementation

### Extend the Platform
- Add new microservices
- Implement additional event types
- Add authentication and authorization
- Implement distributed tracing
- Add monitoring and alerting

## 🆘 Support

### Getting Help
- Check the [README.md](README.md) for comprehensive documentation
- Review [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
- Examine service logs for error details
- Verify all prerequisites are installed

### Common Commands Reference

```bash
# Build and start
./build.sh && docker-compose up -d

# View logs
docker-compose logs -f

# Restart specific service
docker-compose restart order-service

# Scale service
docker-compose up -d --scale order-service=2

# Check resource usage
docker stats

# Access service shell
docker exec -it order-service /bin/bash
```

## 🎉 Congratulations!

You now have a fully functional Event-Driven Microservices Platform running! 

The platform demonstrates:
- ✅ **4 Microservices** with event-driven communication
- ✅ **Kafka** for message streaming
- ✅ **Redis** for caching
- ✅ **Eureka** for service discovery
- ✅ **API Gateway** for routing
- ✅ **Docker** containerization
- ✅ **Saga Pattern** for distributed transactions
- ✅ **Health monitoring** and metrics
- ✅ **OpenAPI documentation**

Happy coding! 🚀 