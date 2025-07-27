package com.example.rabbitmq.pointtopoint;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final RabbitTemplate rabbitTemplate;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Order createOrder(String customerId, String customerEmail, 
                           List<Order.OrderItem> items) {
        
        // Calculate total amount
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order(
                UUID.randomUUID().toString(),
                customerId,  
                customerEmail,
                items,
                totalAmount
        );

        logger.info("Creating order: {}", order);

        // Send order to processing queue (Point-to-Point pattern)
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.ORDER_QUEUE, order);
            logger.info("Order {} sent to processing queue", order.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to send order to queue: {}", e.getMessage());
            throw new RuntimeException("Failed to process order", e);
        }

        return order;
    }

    public Order createSampleOrder() {
        List<Order.OrderItem> items = List.of(
                new Order.OrderItem("prod-1", "Laptop", 1, new BigDecimal("999.99")),
                new Order.OrderItem("prod-2", "Mouse", 2, new BigDecimal("29.99"))
        );

        return createOrder("customer-123", "john.doe@example.com", items);
    }
}