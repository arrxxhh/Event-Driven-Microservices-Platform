# Windows Setup Guide - Event-Driven Microservices Platform

## 🎯 Quick Start for Windows

### Prerequisites Check

Before starting, ensure you have the following installed:

```cmd
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.8+)
mvn -version

# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version
```

### Java Version Management

Since you're using Java 11 for PySpark and Java 17 for this platform, use these convenience scripts:

#### Option 1: Use Convenience Scripts (Recommended)

**For Event-Driven Platform (Java 17):**
```cmd
# Double-click or run:
use-java17.cmd
```

**For PySpark (Java 11):**
```cmd
# Double-click or run:
use-java11.cmd
```

#### Option 2: Manual Setup

**Set Java 17 for this session:**
```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
```

**Set Java 11 for PySpark:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-11
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
```

## 🚀 One-Command Setup

### Step 1: Build and Start Everything

```cmd
# Navigate to project directory
cd event-driven-platform

# Set Java 17 environment
use-java17.cmd

# Build and start everything
build.bat && docker-compose up -d
```

### Step 2: Verify Installation

```cmd
# Check all containers are running
docker-compose ps

# Check service health
curl http://localhost:8080/actuator/health
curl http://localhost:8761/actuator/health
```

## 🔧 Windows-Specific Configuration

### Docker Desktop Setup

1. **Install Docker Desktop for Windows**
   - Download from: https://www.docker.com/products/docker-desktop
   - Enable WSL 2 backend (recommended)
   - Allocate at least 8GB RAM to Docker

2. **Docker Desktop Settings**
   ```
   Resources > Memory: 8GB minimum
   Resources > CPUs: 4 cores minimum
   Resources > Disk: 50GB minimum
   ```

### PowerShell vs Command Prompt

**PowerShell (Recommended):**
```powershell
# Test API with PowerShell
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

**Command Prompt:**
```cmd
# Test API with curl (if installed)
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d "{\"customerId\":\"customer123\",\"items\":[{\"productId\":\"product1\",\"quantity\":2,\"unitPrice\":25.00}],\"totalAmount\":50.00,\"shippingAddress\":\"123 Test St\",\"paymentMethod\":\"CREDIT_CARD\"}"
```

## 🛠️ Common Windows Issues & Solutions

### Issue 1: Port Already in Use

**Problem**: `Error: Port 8080 is already in use`

**Solution**:
```cmd
# Check what's using the port
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F

# Or stop all Java processes
taskkill /F /IM java.exe
```

### Issue 2: Docker Desktop Not Starting

**Problem**: Docker Desktop fails to start

**Solutions**:
1. **Restart Docker Desktop**
2. **Check Windows Services**:
   ```cmd
   services.msc
   # Ensure "Docker Desktop Service" is running
   ```
3. **Reset Docker Desktop**:
   - Docker Desktop → Settings → Troubleshoot → Reset to factory defaults

### Issue 3: Maven Build Fails

**Problem**: `[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin`

**Solutions**:
1. **Check Java version**:
   ```cmd
   java -version
   mvn -version
   ```
2. **Clean and rebuild**:
   ```cmd
   mvn clean install
   ```
3. **Delete target folders**:
   ```cmd
   rmdir /s /q target
   mvn clean install
   ```

### Issue 4: Docker Compose Issues

**Problem**: `docker-compose: command not found`

**Solution**:
```cmd
# Use Docker Desktop's built-in compose
docker compose up -d

# Or install Docker Compose separately
# Download from: https://docs.docker.com/compose/install/
```

### Issue 5: Memory Issues

**Problem**: `OutOfMemoryError: Java heap space`

**Solutions**:
1. **Increase Docker memory allocation**
2. **Set Maven memory**:
   ```cmd
   set MAVEN_OPTS=-Xmx2g -XX:MaxPermSize=512m
   ```
3. **Reduce concurrent builds**:
   ```cmd
   mvn clean install -T 1C
   ```

## 📊 Monitoring on Windows

### Service Health Checks

```cmd
# Check all services
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8761/actuator/health
```

### Docker Monitoring

```cmd
# View running containers
docker ps

# View container logs
docker-compose logs -f order-service

# Monitor resource usage
docker stats

# Check disk usage
docker system df
```

### Kafka Monitoring

```cmd
# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Monitor messages
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-events --from-beginning
```

## 🔄 Development Workflow

### Daily Development Setup

```cmd
# 1. Start with Java 17
use-java17.cmd

# 2. Build services (if code changed)
build.bat

# 3. Start platform
docker-compose up -d

# 4. Monitor logs
docker-compose logs -f
```

### Switching Between Projects

```cmd
# For Event-Driven Platform (Java 17)
use-java17.cmd
cd event-driven-platform
docker-compose up -d

# For PySpark (Java 11)
use-java11.cmd
cd pyspark-project
# Your PySpark commands here
```

### Stopping and Cleaning

```cmd
# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Remove all containers and images
docker system prune -a
```

## 🎯 Access Points

Once the platform is running, access these URLs in your browser:

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Kafka UI**: http://localhost:8085

## 📝 Useful Commands Reference

### Build Commands
```cmd
build.bat                    # Build all services
mvn clean install           # Manual build
mvn clean package           # Package without installing
```

### Docker Commands
```cmd
docker-compose up -d        # Start all services
docker-compose down         # Stop all services
docker-compose restart      # Restart all services
docker-compose logs -f      # Follow logs
docker-compose ps           # Check status
```

### Testing Commands
```cmd
# Test order creation
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d "{\"customerId\":\"test\",\"items\":[{\"productId\":\"product1\",\"quantity\":1,\"unitPrice\":10.00}],\"totalAmount\":10.00,\"shippingAddress\":\"test\",\"paymentMethod\":\"CREDIT_CARD\"}"

# Test order retrieval
curl http://localhost:8080/orders

# Test inventory
curl http://localhost:8080/inventory/product1
```

## 🆘 Troubleshooting

### If Nothing Works

1. **Complete Reset**:
   ```cmd
   docker-compose down -v
   docker system prune -a
   docker volume prune
   ```

2. **Rebuild Everything**:
   ```cmd
   use-java17.cmd
   build.bat
   docker-compose up -d
   ```

3. **Check System Resources**:
   - Ensure Docker Desktop has enough memory (8GB+)
   - Close other resource-intensive applications
   - Restart Docker Desktop

### Getting Help

1. **Check logs first**:
   ```cmd
   docker-compose logs
   ```

2. **Verify prerequisites**:
   ```cmd
   java -version
   mvn -version
   docker --version
   ```

3. **Check service health**:
   ```cmd
   curl http://localhost:8080/actuator/health
   ```

---

## ✅ Success Checklist

- [ ] Java 17 is set as JAVA_HOME
- [ ] Maven is using Java 17
- [ ] Docker Desktop is running
- [ ] All containers are started
- [ ] Health checks pass
- [ ] Can access API Gateway
- [ ] Can create an order
- [ ] Can view Eureka dashboard

**You're all set! 🚀** 