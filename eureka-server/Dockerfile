FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/eureka-server-1.0.0.jar app.jar

# Expose port
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 