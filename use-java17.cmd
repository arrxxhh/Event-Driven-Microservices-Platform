@echo off
echo ========================================
echo Setting up Java 17 for Event-Driven Platform
echo ========================================
echo.

:: Set Java 17 environment
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

:: Verify Java version
echo Current Java version:
java -version
echo.

:: Verify Maven version
echo Current Maven version:
mvn -version
echo.

:: Check if we're in the right directory
if not exist "pom.xml" (
    echo WARNING: pom.xml not found in current directory
    echo Please navigate to the event-driven-platform directory
    echo.
)

echo ========================================
echo Environment ready for building!
echo ========================================
echo.
echo Available commands:
echo   build.bat                    - Build all services
echo   docker-compose up -d         - Start the platform
echo   docker-compose down          - Stop the platform
echo   docker-compose logs -f       - View logs
echo.
echo Access points:
echo   API Gateway: http://localhost:8080
echo   Eureka Dashboard: http://localhost:8761
echo   Swagger UI: http://localhost:8080/swagger-ui.html
echo   Kafka UI: http://localhost:8085
echo.

:: Keep the command prompt open
cmd /k 