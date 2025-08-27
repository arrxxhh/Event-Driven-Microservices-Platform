@echo off
echo 🚀 Building Event-Driven Microservices Platform...

echo 📦 Building all modules from parent directory...
call mvn clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo ✅ All services built successfully!
echo.
echo 🐳 To start the platform, run:
echo    docker-compose up -d
echo.
echo 📊 Access points:
echo    - API Gateway: http://localhost:8080
echo    - Eureka Dashboard: http://localhost:8761
echo    - Kafka UI: http://localhost:8085
echo    - Swagger UI: http://localhost:8080/swagger-ui.html

pause 