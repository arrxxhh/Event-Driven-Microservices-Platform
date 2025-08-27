FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/inventory-service-1.0.0.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 