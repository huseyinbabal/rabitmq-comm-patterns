# RabbitMQ Communication Patterns with Spring Boot

A comprehensive demonstration of RabbitMQ messaging patterns using Spring Boot, featuring real-world use cases and best practices for building scalable, message-driven applications.

## 🎯 Project Overview

This project demonstrates five fundamental RabbitMQ communication patterns using **RabbitMQ 4.0** with its latest features and performance improvements:

1. **Point-to-Point (Queue)** - Order Processing System
2. **Publish/Subscribe (Fanout)** - Social Media Notifications
3. **Direct Exchange (Routing)** - Log Processing System
4. **Topic Exchange** - IoT Device Management
5. **Work Queue** - Image Processing Service

Each pattern includes production-ready examples with error handling, monitoring, and best practices.

### 🆕 RabbitMQ 4.0 Features Utilized

- **Enhanced Performance:** Improved message throughput and reduced latency
- **Native Prometheus Metrics:** Built-in metrics endpoint without additional plugins
- **Better Resource Management:** Optimized memory and CPU usage
- **Improved Clustering:** Enhanced cluster formation and partition handling
- **Advanced Queue Features:** Better priority queue support and consumer priorities
- **Modern Management UI:** Updated interface with better performance insights

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Communication Patterns](#communication-patterns)
- [API Documentation](#api-documentation)
- [Running the Application](#running-the-application)
- [Testing the Patterns](#testing-the-patterns)
- [Monitoring](#monitoring)
- [Best Practices](#best-practices)
- [Production Considerations](#production-considerations)

## 🛠 Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **RabbitMQ Server** (Docker recommended)
- **Docker & Docker Compose** (optional, for easy setup)

## 🚀 Quick Start

### 1. Start RabbitMQ Server & Prometheus & Grafana Stack

**Using Docker:**

```bash
docker compose -f docker-infrastructure.yaml up -d
```

**Access RabbitMQ Management UI:** http://localhost:15672 (guest/guest)

### 2. Clone and Run the Application

```bash
git clone <repository-url>
cd rabbitmq-comm-patterns
mvn spring-boot:run
```

### 3. Access the Application

- **Application:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health

## 📨 Communication Patterns

### 1. Point-to-Point Pattern (Order Processing)

**Use Case:** E-commerce order payment processing

- **Exchange:** Default (direct)
- **Queue:** `order.processing.queue`
- **Pattern:** One producer → One queue → One consumer

**Real-world Example:**

```bash
# Create a sample order
curl -X POST http://localhost:8080/api/orders/sample
```

**How it works:**

1. Order service sends order to payment queue
2. Payment service processes payment (one order = one processor)
3. Ensures no duplicate payments

### 2. Publish/Subscribe Pattern (Social Media)

**Use Case:** Social media post distribution

- **Exchange:** `social.fanout.exchange` (fanout)
- **Queues:** Timeline, Notifications, Analytics
- **Pattern:** One producer → Multiple consumers

**Real-world Example:**

```bash
# Publish a social media post
curl -X POST http://localhost:8080/api/social/posts/sample
```

**How it works:**

1. User posts content
2. All services (timeline, notifications, analytics) receive the post
3. Each service processes independently

### 3. Direct Exchange Pattern (Log Processing)

**Use Case:** Application log routing by severity

- **Exchange:** `log.direct.exchange` (direct)
- **Routing Keys:** `error`, `warning`, `info`, `debug`
- **Pattern:** Messages routed by exact key match

**Real-world Example:**

```bash
# Generate sample logs
curl -X POST http://localhost:8080/api/logs/sample-logs
```

**How it works:**

1. Applications send logs with severity levels
2. Error logs → Alert service (immediate attention)
3. Warning logs → Monitoring service
4. Info logs → Analytics service

### 4. Topic Exchange Pattern (IoT Management)

**Use Case:** Smart home device telemetry

- **Exchange:** `iot.topic.exchange` (topic)
- **Routing Patterns:** `sensor.*.*`, `device.*.battery`
- **Pattern:** Wildcard-based routing

**Real-world Example:**

```bash
# Send IoT sensor data
curl -X POST http://localhost:8080/api/iot/sample-data
```

**How it works:**

1. IoT devices send telemetry with hierarchical routing keys
2. `sensor.temperature.livingroom` → HVAC system
3. `device.*.battery` → Battery monitoring service
4. `sensor.#` → Analytics platform

### 5. Work Queue Pattern (Image Processing)

**Use Case:** Distributed image processing

- **Exchange:** Default
- **Queue:** `image.processing.queue`
- **Pattern:** Multiple workers compete for tasks

**Real-world Example:**

```bash
# Submit batch image processing
curl -X POST http://localhost:8080/api/images/batch-process
```

**How it works:**

1. Users upload images for processing
2. Multiple worker instances process in parallel
3. Round-robin distribution ensures load balancing

## 📖 API Documentation

### Interactive API Documentation

Access the complete API documentation at: http://localhost:8080/swagger-ui.html

### Key Endpoints

#### Order Processing (Point-to-Point)

- `POST /api/orders` - Create new order
- `POST /api/orders/sample` - Create sample order

#### Social Media (Publish/Subscribe)

- `POST /api/social/posts` - Create social post
- `GET /api/social/timeline/{userId}` - Get user timeline
- `GET /api/social/analytics/summary` - Get analytics summary

#### Log Processing (Direct Exchange)

- `POST /api/logs/error` - Log error message
- `POST /api/logs/warning` - Log warning message
- `POST /api/logs/info` - Log info message
- `GET /api/logs/analytics/summary` - Get log analytics

#### IoT Management (Topic Exchange)

- `POST /api/iot/sensor/temperature` - Send temperature data
- `POST /api/iot/sensor/motion` - Send motion data
- `POST /api/iot/device/battery` - Send battery status

#### Image Processing (Work Queue)

- `POST /api/images/process` - Submit single image
- `POST /api/images/batch-process` - Submit batch images

## 🏃‍♂️ Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

### Production Mode

```bash
mvn clean package
java -jar target/rabbitmq-comm-patterns-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing the Patterns

### 1. Test Order Processing

```bash
# Create orders and watch payment processing logs
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/orders/sample
  sleep 2
done
```

### 2. Test Social Media Broadcasting

```bash
# Post content and see fanout to all services
curl -X POST http://localhost:8080/api/social/posts/sample
curl -X POST http://localhost:8080/api/social/posts/text-sample
```

### 3. Test Log Routing

```bash
# Generate logs at different levels
curl -X POST http://localhost:8080/api/logs/sample-logs
# Check analytics
curl http://localhost:8080/api/logs/analytics/summary
```

### 4. Test IoT Routing Patterns

```bash
# Send various IoT messages
curl -X POST http://localhost:8080/api/iot/sample-data
```

### 5. Test Work Queue Load Balancing

```bash
# Submit batch processing and watch worker distribution
curl -X POST http://localhost:8080/api/images/batch-process
```

## 📊 Monitoring

### RabbitMQ Management UI

- **URL:** http://localhost:15672
- **Username:** guest
- **Password:** guest

### RabbitMQ 4 Prometheus Metrics (New!)

- **URL:** http://localhost:15692/metrics
- **Integration:** Direct Prometheus scraping without plugins

**Key Metrics to Monitor:**

- Queue depths and message rates
- Consumer utilization
- Connection and channel counts
- Memory and disk usage
- **New in RabbitMQ 4:** Enhanced stream performance metrics
- **New in RabbitMQ 4:** Improved clustering metrics

### Application Metrics

- **Health Check:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Prometheus:** http://localhost:8080/actuator/prometheus

### Log Monitoring

The application provides structured logging with different levels:

- **ERROR:** Critical issues requiring immediate attention
- **WARN:** Performance and monitoring alerts
- **INFO:** Business events and analytics
- **DEBUG:** Detailed processing information

## 🏆 Best Practices Implemented

### Message Design

- ✅ **Immutable Messages:** All message objects are designed to be immutable
- ✅ **JSON Serialization:** Consistent JSON message format across all patterns
- ✅ **Message Versioning:** Prepared for future schema evolution
- ✅ **Correlation IDs:** Every message has unique identifiers for tracing

### Error Handling

- ✅ **Dead Letter Exchanges:** Failed messages routed to DLX for investigation
- ✅ **Retry Logic:** Exponential backoff with maximum retry limits
- ✅ **Circuit Breakers:** Prevent cascade failures
- ✅ **Graceful Degradation:** Fallback mechanisms for service failures

### Performance

- ✅ **Connection Pooling:** Reuse connections efficiently
- ✅ **Message Acknowledgments:** Manual acknowledgments for reliability
- ✅ **Prefetch Limits:** Optimal message distribution
- ✅ **Async Processing:** Non-blocking message processing

### Security

- ✅ **Input Validation:** All request payloads validated
- ✅ **No Secrets in Logs:** Sensitive data properly masked
- ✅ **Connection Security:** Prepared for TLS/SSL configuration

### Observability

- ✅ **Structured Logging:** JSON-formatted logs with correlation IDs
- ✅ **Metrics Collection:** Prometheus-compatible metrics
- ✅ **Health Checks:** Comprehensive health monitoring
- ✅ **Tracing Ready:** Prepared for distributed tracing integration

## 🏭 Production Considerations

### Configuration

```yaml
# application-prod.yml
spring:
  rabbitmq:
    host: rabbitmq-cluster.internal
    port: 5672
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    ssl:
      enabled: true
    connection-timeout: 30s
    publisher-confirm-type: correlated
    publisher-returns: true
```

### Infrastructure Requirements

- **RabbitMQ Cluster:** 3+ nodes for high availability
- **Load Balancer:** HAProxy or similar for connection load balancing
- **Monitoring:** Prometheus + Grafana for metrics visualization
- **Log Aggregation:** ELK Stack or similar for log analysis

### Scaling Strategies

1. **Horizontal Scaling:** Add more consumer instances
2. **Queue Sharding:** Distribute load across multiple queues
3. **Message Partitioning:** Route by customer/tenant ID
4. **Auto Scaling:** Scale based on queue depth metrics

### Security Checklist

- [ ] Enable TLS/SSL for all connections
- [ ] Implement proper authentication/authorization
- [ ] Use dedicated users for different services
- [ ] Enable audit logging
- [ ] Regular security updates and patches

## 🔧 Configuration Reference

### Key Application Properties

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: 10
        retry:
          enabled: true
          max-attempts: 3
```

### Environment Variables

- `RABBITMQ_HOST` - RabbitMQ server host
- `RABBITMQ_USERNAME` - RabbitMQ username
- `RABBITMQ_PASSWORD` - RabbitMQ password
- `LOG_LEVEL` - Application log level

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📚 Additional Resources

- [RabbitMQ Official Documentation](https://rabbitmq.com/documentation.html)
- [Spring AMQP Reference](https://docs.spring.io/spring-amqp/reference/html/)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)
- [Message Queue Patterns](https://microservices.io/patterns/data/saga.html)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎓 Learning Objectives

After working through this project, you will understand:

- When to use each RabbitMQ communication pattern
- How to implement reliable message processing
- Best practices for error handling and monitoring
- Production deployment considerations
- Performance optimization techniques

---

**Happy Learning! 🚀**

For questions or support, please open an issue in the GitHub repository.
