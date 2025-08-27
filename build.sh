#!/bin/bash

echo "🚀 Building Event-Driven Microservices Platform..."

# Set Java version
export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}

echo "📦 Building all modules from parent directory..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Please check the errors above."
    exit 1
fi

echo "✅ All services built successfully!"
echo ""
echo "🐳 To start the platform, run:"
echo "   docker-compose up -d"
echo ""
echo "📊 Access points:"
echo "   - API Gateway: http://localhost:8080"
echo "   - Eureka Dashboard: http://localhost:8761"
echo "   - Kafka UI: http://localhost:8085"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html" 