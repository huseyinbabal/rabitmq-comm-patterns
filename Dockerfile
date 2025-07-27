FROM openjdk:17-jdk-slim

LABEL maintainer="RabbitMQ Communication Patterns Demo"
LABEL description="Spring Boot application demonstrating RabbitMQ patterns"

WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "target/rabbitmq-comm-patterns-0.0.1-SNAPSHOT.jar"]