---
marp: true
theme: default
paginate: true
style: |
  section {
    font-size: 1.2em;
  }
  h1 {
    font-size: 2.2em;
  }
  li {
    font-size: 1.2em;
  }
---
# RabbitMQ Communication Patterns
## A Comprehensive Guide with Spring Boot Implementation

---

## Slide 1: Title Slide
**RabbitMQ Communication Patterns**
*Mastering Message-Driven Architecture with Spring Boot*

Presenter: Huseyin BABAL

---

## Slide 2: Agenda
- Introduction to Messaging & RabbitMQ
- Core Communication Patterns Overview
- Point-to-Point (Queue) Pattern
- Publish/Subscribe (Fanout) Pattern
- Direct Exchange (Routing) Pattern
- Topic Exchange Pattern
- Work Queue Pattern
- Best Practices & Error Handling
- Live Coding Demonstrations
- Q&A Session

---

## Slide 3: What is Message-Driven Architecture?
- **Asynchronous Communication**: Systems communicate without waiting for immediate responses
- **Decoupling**: Components don't need to know about each other directly
- **Scalability**: Easy to add new consumers or producers
- **Reliability**: Messages can be persisted and guaranteed delivery
- **Flexibility**: Different protocols, formats, and routing strategies

---

## Slide 4: Why RabbitMQ 4.0?
- **Battle-tested**: Used by companies like Instagram, Reddit, NASA
- **Feature-rich**: Multiple exchange types, routing, clustering
- **Cross-platform**: Supports 10+ programming languages
- **Enhanced Performance**: RabbitMQ 4.0 delivers up to 50% better throughput
- **Native Prometheus**: Built-in metrics without additional plugins
- **Modern Management UI**: Redesigned interface with better insights
- **Improved Clustering**: Better partition handling and recovery

---

## Slide 5: RabbitMQ Core Concepts
- **Producer**: Application that sends messages
- **Queue**: Buffer that stores messages
- **Consumer**: Application that receives messages
- **Exchange**: Routes messages to queues based on rules
- **Binding**: Link between exchange and queue
- **Routing Key**: Message attribute used for routing

---

## Slide 6: Communication Patterns Overview
1. **Point-to-Point (Queue)**: Direct message delivery
2. **Publish/Subscribe (Fanout)**: Broadcast to all subscribers
3. **Direct Exchange**: Route by exact routing key match
4. **Topic Exchange**: Route by pattern matching
5. **Work Queue**: Distribute tasks among workers

---

## Slide 7: Pattern 1 - Point-to-Point (Queue)
### Direct Communication Between Producer and Consumer

**Characteristics:**
- One-to-one communication
- Message consumed by exactly one consumer
- Simple and reliable
- Default exchange type in many scenarios

---

## Slide 8: Point-to-Point - Real-World Use Case
### **E-commerce Order Processing**

**Scenario**: When a customer places an order
- Order service sends order details to payment queue
- Payment service processes the payment
- Only one payment processor handles each order
- Ensures no duplicate payments

**Benefits**: Guaranteed single processing, simple workflow

---

## Slide 9: Point-to-Point - Architecture Diagram
```
                  POINT-TO-POINT PATTERN
                (E-commerce Order Processing)

  ┌───────────┐      ┌─────────────┐      ┌─────────────┐
  │Order Svc  │─────▶│order.queue  │─────▶│Payment Svc  │
  │(Producer) │      │             │      │ (Consumer)  │
  └───────────┘      └─────────────┘      └─────────────┘

  Message: { "orderId": "12345", "amount": "$999" }
                           │
                           ▼
                     ✅ ONE-TO-ONE
                  Only ONE consumer gets
                   each message (no copies)
```

**Key Characteristics:**
• **Single Consumer** - Each message processed by exactly one service
• **Reliable** - Manual acknowledgments ensure message delivery  
• **Simple** - Direct producer → queue → consumer flow
• **Use Case** - Financial transactions, critical operations

---

## Slide 10: Pattern 2 - Publish/Subscribe (Fanout)
### Broadcast Messages to All Subscribers

**Characteristics:**
- One-to-many communication
- All bound queues receive the message
- No routing key required
- Perfect for notifications and events

---

## Slide 11: Publish/Subscribe - Real-World Use Case
### **Social Media Post Notifications**

**Scenario**: User posts a new photo
- Post service publishes to fanout exchange
- Timeline service updates user feeds
- Notification service sends push notifications
- Analytics service records engagement data
- Search service indexes content

**Benefits**: Multiple services react to single event

---

## Slide 12: Publish/Subscribe - Architecture Diagram
```
                    PUBLISH/SUBSCRIBE PATTERN
                   (Social Media Broadcasting)

                   ┌─────────────┐
                   │ Post Service│
                   │(Publisher)  │
                   └─────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │Fanout Exchg │
                   │(Broadcasts) │
                   └─────────────┘
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
        ┌─────────┐ ┌─────────┐ ┌─────────┐
        │Timeline │ │Notify   │ │Analytics│
        │Service  │ │Service  │ │Service  │
        └─────────┘ └─────────┘ └─────────┘

  Message: { "postId": "789", "content": "New post!" }
                          │
                          ▼
                   📡 ONE-TO-MANY
               Same message copied to ALL
                  bound services
```

---

## Slide 12: Publish/Subscribe - Architecture Diagram
**Key Characteristics:**
• **Broadcasting** - One message sent to ALL subscribers
• **Independent** - Each service processes message separately
• **Parallel** - All services work simultaneously  
• **Use Case** - Event notifications, real-time updates

---

## Slide 13: Pattern 3 - Direct Exchange (Routing)
### Route Messages by Exact Routing Key Match

**Characteristics:**
- Routes based on exact routing key match
- Multiple queues can have same routing key
- More selective than fanout
- Default exchange behavior

---

## Slide 14: Direct Exchange - Real-World Use Case
### **Log Processing System**

**Scenario**: Application generates different log levels
- Error logs → error.queue (handled by alert service)
- Warning logs → warning.queue (handled by monitoring)
- Info logs → info.queue (handled by analytics)
- Debug logs → debug.queue (handled by debugging tools)

**Benefits**: Efficient log categorization and processing

---

## Slide 15: Direct Exchange - Architecture Diagram
```
                     DIRECT EXCHANGE PATTERN
                       (Log Processing)

                    ┌───────────┐
                    │App Services│
                    │(Producers) │
                    └───────────┘
                          │
                          ▼
                    ┌───────────┐
                    │Direct Exchg│
                    │(Routes by  │
                    │    key)    │
                    └───────────┘
                          │
      key="error" key="warning" key="info" key="debug"
           ▼           ▼          ▼          ▼
      ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐
      │ Alert  │  │Monitor │  │Analytics│  │ Debug  │
      │Service │  │Service │  │Service  │  │ Tools  │
      └────────┘  └────────┘  └────────┘  └────────┘

    Routing Examples:
    • "error"   → Alert Service (🚨 Critical)
    • "warning" → Monitor (📊 Track)
    • "info"    → Analytics (📈 Metrics)
    • "debug"   → Debug Tools (🔧 Dev)
```

---

## Slide 15: Direct Exchange - Architecture Diagram
**Key Characteristics:**
• **Exact Match** - Routes by exact routing key match
• **Selective** - More targeted than fanout broadcasting
• **Flexible** - Multiple queues can use same routing key
• **Use Case** - Log levels, message categories, priorities

---

## Slide 16: Pattern 4 - Topic Exchange
### Route Messages by Pattern Matching

**Characteristics:**
- Routes based on wildcard patterns
- Uses . (dot) as delimiter
- * matches exactly one word
- # matches zero or more words
- Most flexible routing

---

## Slide 17: Topic Exchange - Real-World Use Case
### **IoT Device Management System**

**Scenario**: Smart home devices send telemetry data
- `sensor.temperature.livingroom` → HVAC system
- `sensor.motion.*` → Security system  
- `device.*.battery` → Battery monitoring
- `sensor.#` → Data analytics platform
- `device.camera.#` → Video processing

**Benefits**: Flexible, hierarchical message routing

---

## Slide 18: Topic Exchange - Architecture Diagram
```
                      TOPIC EXCHANGE PATTERN
                        (IoT Smart Home)

                      ┌─────────────┐
                      │ IoT Devices │
                      │(Publishers) │
                      └─────────────┘
                            │
                            ▼
                      ┌─────────────┐
                      │Topic Exchange│
                      │(Pattern     │
                      │ Match)      │
                      └─────────────┘
                            │
         ┌──────────────────┼──────────────────┐
         ▼                  ▼                  ▼
   ┌─────────┐        ┌─────────┐        ┌─────────┐
   │  HVAC   │        │Security │        │Analytics│
   │ Service │        │Service  │        │Service  │
   └─────────┘        └─────────┘        └─────────┘

  Routing Pattern Examples:
  🌡️ "sensor.temperature.*" → HVAC Service
  🔒 "sensor.motion.*"      → Security Service  
  🔋 "device.*.battery"     → Battery Monitor
  📊 "sensor.#"             → Analytics (all sensors)

  Wildcards: * = exactly 1 word   # = 0+ words
```

---

## Slide 18: Topic Exchange - Architecture Diagram
**Key Characteristics:**
• **Pattern Match** - Routes using wildcards (* and #)
• **Hierarchical** - Uses dot-separated routing keys
• **Flexible** - One message can match multiple patterns
• **Use Case** - IoT systems, categorized data, overlapping interests

---

## Slide 19: Pattern 5 - Work Queue
### Distribute Tasks Among Multiple Workers

**Characteristics:**
- Multiple consumers compete for messages
- Load balancing across workers
- Built-in round-robin distribution
- Perfect for CPU-intensive tasks

---

## Slide 20: Work Queue - Real-World Use Case
### **Image Processing Service**

**Scenario**: Users upload images for processing
- Resize images for different devices
- Apply filters and effects
- Generate thumbnails
- Extract metadata
- Multiple worker instances process in parallel

**Benefits**: Horizontal scaling, fault tolerance

---

## Slide 21: Work Queue - Architecture Diagram
```
                       WORK QUEUE PATTERN
                     (Image Processing)

                    ┌─────────────┐
                    │Upload Service│
                    │ (Producer)  │
                    └─────────────┘
                          │
                          ▼
                    ┌─────────────┐
                    │ Work Queue  │
                    │(Task Queue) │
                    └─────────────┘
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
        ┌─────────┐ ┌─────────┐ ┌─────────┐
        │Worker 1 │ │Worker 2 │ │Worker N │
        │ (Busy)  │ │(Available)│ (Busy)  │
        └─────────┘ └─────────┘ └─────────┘

  Processing Tasks:       🔄 Round-Robin Distribution
  • Resize images        • Fair task assignment
  • Apply filters        • Load balancing
  • Generate thumbnails   • Horizontal scaling
  • Upload to CDN
```

---

## Slide 21: Work Queue - Architecture Diagram
**Key Characteristics:**
• **Multiple Workers** - Consumers compete for tasks
• **Load Balancing** - Round-robin distribution
• **Scalable** - Add/remove workers dynamically
• **Use Case** - CPU-intensive tasks, batch processing

---

## Slide 22: Spring Boot + RabbitMQ 4.0 Integration
### Optimized Configuration for RabbitMQ 4.0

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**Key Annotations:**
- `@RabbitListener` - Message consumer with priority support
- `@RabbitTemplate` - Message producer with enhanced caching
- `@EnableRabbit` - Enable RabbitMQ 4.0 features

**RabbitMQ 4.0 Optimizations:**
- Connection and channel pooling
- Consumer priority configuration
- Native Prometheus metrics integration

---

## Slide 23: Production Best Practices

**Reliability & Error Handling**
• Message Persistence - Survive broker restarts
• Manual Acknowledgments - Confirm processing  
• Dead Letter Queues - Handle failed messages
• Retry Logic - Exponential backoff strategy

**Scalability & Performance**
• Connection Pooling - Reuse connections efficiently
• Consumer Scaling - Auto-scale based on queue depth
• Message Size - Keep payloads reasonably small
• Prefetch Limits - Prevent consumer overload

**Monitoring & Operations**
• Queue Depths - Track message backlogs
• Processing Rates - Monitor throughput
• Error Rates - Alert on failures
• Resource Usage - Memory and CPU monitoring

---

## Slide 24: Common Pitfalls to Avoid
• **Memory Leaks** - Not closing connections properly
• **Queue Overflow** - No message TTL or consumer lag
• **Blocking Operations** - Synchronous processing in consumers
• **No Error Handling** - Messages lost on exceptions
• **Poor Monitoring** - No visibility into system health

---

## Slide 25: Live Demo Overview
**What We'll Build Together**

**E-commerce Microservices System:**
• Order processing (Point-to-Point)
• Notification system (Publish/Subscribe)  
• Log routing (Direct Exchange)
• IoT telemetry (Topic Exchange)
• Image processing (Work Queue)

---

## Slide 26: Production Considerations
• **Security** - Authentication, authorization, SSL/TLS
• **Performance Tuning** - Prefetch count, batch processing
• **Backup & Recovery** - Message and configuration backup
• **Upgrade Strategy** - Zero-downtime deployments
• **Cost Optimization** - Resource allocation and monitoring

---

## Slide 27: RabbitMQ 4.0 Monitoring & Metrics
**Native Prometheus Integration**

**Built-in Metrics Endpoint:**
```bash
curl http://localhost:15692/metrics
```

**Key Improvements:**
• **No Plugin Required** - Metrics built into core
• **Better Performance** - Reduced overhead for metric collection
• **Enhanced Metrics** - New queue, connection, and cluster metrics
• **Grafana Ready** - Direct integration with monitoring dashboards

**Sample Metrics:**
• `rabbitmq_queue_messages_total`
• `rabbitmq_connections_opened_total` 
• `rabbitmq_consumer_utilisation`

---

## Slide 28: Resources & Next Steps
• **Official Documentation** - rabbitmq.com/documentation.html
• **Spring AMQP Guide** - spring.io/guides/gs/messaging-rabbitmq
• **GitHub Repository** - [Your project repo]
• **Monitoring Tools** - Prometheus, Grafana, RabbitMQ Management
• **Books** - "RabbitMQ in Action", "Enterprise Integration Patterns"

---

## Slide 29: Q&A Session
**Questions & Discussion**

**Common Questions:**
• When to choose RabbitMQ vs Apache Kafka?
• How to handle message ordering?
• Scaling strategies for high throughput?
• Monitoring and alerting best practices?
• Integration with cloud services?

**Thank you for your attention!**

---

*This presentation includes live coding demonstrations of each pattern using Spring Boot. The complete source code is available in the accompanying GitHub repository.*