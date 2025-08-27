#!/bin/bash

echo "🧪 Testing Event-Driven Microservices Platform..."

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Test Eureka Server
echo "🔍 Testing Eureka Server..."
curl -s http://localhost:8761/actuator/health | jq . || echo "Eureka Server not ready"

# Test API Gateway
echo "🌐 Testing API Gateway..."
curl -s http://localhost:8080/actuator/health | jq . || echo "API Gateway not ready"

# Test Order Service
echo "📋 Testing Order Service..."
curl -s http://localhost:8081/actuator/health | jq . || echo "Order Service not ready"

# Test Inventory Service
echo "📦 Testing Inventory Service..."
curl -s http://localhost:8082/actuator/health | jq . || echo "Inventory Service not ready"

# Test Payment Service
echo "💳 Testing Payment Service..."
curl -s http://localhost:8083/actuator/health | jq . || echo "Payment Service not ready"

# Test Notification Service
echo "🔔 Testing Notification Service..."
curl -s http://localhost:8084/actuator/health | jq . || echo "Notification Service not ready"

# Create a test order
echo "📝 Creating test order..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/orders \
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
  }')

echo "📋 Order Response:"
echo $ORDER_RESPONSE | jq .

# Extract order ID
ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.orderId')

if [ "$ORDER_ID" != "null" ] && [ "$ORDER_ID" != "" ]; then
    echo "✅ Order created successfully with ID: $ORDER_ID"
    
    # Wait for event processing
    echo "⏳ Waiting for event processing..."
    sleep 10
    
    # Get order status
    echo "📋 Getting order status..."
    curl -s http://localhost:8080/orders/$ORDER_ID | jq .
    
    # Get all orders
    echo "📋 Getting all orders..."
    curl -s http://localhost:8080/orders | jq .
    
else
    echo "❌ Failed to create order"
fi

echo ""
echo "🎉 Platform testing completed!"
echo ""
echo "📊 Access the following URLs to explore:"
echo "   - API Gateway: http://localhost:8080"
echo "   - Eureka Dashboard: http://localhost:8761"
echo "   - Kafka UI: http://localhost:8085"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html" 