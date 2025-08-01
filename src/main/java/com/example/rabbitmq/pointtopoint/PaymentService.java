package com.example.rabbitmq.pointtopoint;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final Random random = new Random();

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void processPayment(Order order) {
        try {
            logger.info("Processing payment for order: {}", order.getOrderId());
            
            // Simulate payment processing time
            Thread.sleep(2000 + random.nextInt(3000));
            
            // Simulate payment success/failure (90% success rate)
            boolean paymentSuccessful = random.nextDouble() < 0.9;
            
            if (paymentSuccessful) {
                order.setStatus(Order.OrderStatus.PAID);
                logger.info("Payment successful for order: {} - Amount: ${}", 
                           order.getOrderId(), order.getTotalAmount());
                
                // Simulate sending confirmation email
                sendPaymentConfirmation(order);
                
            } else {
                logger.warn("Payment failed for order: {}", order.getOrderId());
                order.setStatus(Order.OrderStatus.CANCELLED);
                
                // Simulate sending failure notification
                sendPaymentFailureNotification(order);
                
                // In AUTO mode, throwing an exception will cause message to be redelivered
                // For payment failures, we don't want redelivery, so we just log and continue
            }
            
        } catch (InterruptedException e) {
            logger.error("Payment processing interrupted for order: {}", order.getOrderId());
            Thread.currentThread().interrupt();
            // In AUTO mode, throwing an exception will cause redelivery
            throw new RuntimeException("Payment processing interrupted", e);
        } catch (Exception e) {
            logger.error("Error processing payment for order: {} - Error: {}", 
                        order.getOrderId(), e.getMessage());
            // In AUTO mode, throwing an exception will cause redelivery
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    private void sendPaymentConfirmation(Order order) {
        logger.info("Sending payment confirmation email to: {} for order: {}", 
                   order.getCustomerEmail(), order.getOrderId());
        // In real implementation, this would integrate with email service
    }

    private void sendPaymentFailureNotification(Order order) {
        logger.info("Sending payment failure notification to: {} for order: {}", 
                   order.getCustomerEmail(), order.getOrderId());
        // In real implementation, this would integrate with notification service
    }
}