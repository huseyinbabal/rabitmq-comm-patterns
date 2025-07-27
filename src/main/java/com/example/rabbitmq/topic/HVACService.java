package com.example.rabbitmq.topic;

import com.example.rabbitmq.config.RabbitConfig;
import com.example.rabbitmq.model.IoTMessage;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class HVACService {
    
    private static final Logger logger = LoggerFactory.getLogger(HVACService.class);

    @RabbitListener(queues = RabbitConfig.HVAC_QUEUE)
    public void processTemperatureData(IoTMessage message, Channel channel, Message mqMessage) {
        try {
            logger.info("🌡️ HVAC System processing temperature data from {}: {}°C", 
                       message.getLocation(), message.getValue());
            
            // HVAC decision logic
            if (message.getValue() > 25.0) {
                logger.info("   🧊 Temperature too high - Turning on AC for {}", message.getLocation());
                activateAirConditioning(message.getLocation(), message.getValue());
            } else if (message.getValue() < 18.0) {
                logger.info("   🔥 Temperature too low - Turning on heating for {}", message.getLocation());
                activateHeating(message.getLocation(), message.getValue());
            } else {
                logger.info("   ✅ Temperature optimal for {} - No action needed", message.getLocation());
            }
            
            // Update thermostat settings
            updateThermostat(message);
            
            // Log energy usage
            logEnergyUsage(message);
            
            channel.basicAck(mqMessage.getMessageProperties().getDeliveryTag(), false);
            
        } catch (Exception e) {
            logger.error("Error processing temperature data: {}", e.getMessage());
            try {
                channel.basicNack(mqMessage.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception nackException) {
                logger.error("Failed to nack message", nackException);
            }
        }
    }

    private void activateAirConditioning(String location, double temperature) {
        logger.info("❄️ Activating AC in {} - Target: 23°C (Current: {}°C)", location, temperature);
        // Integration with HVAC control systems
    }

    private void activateHeating(String location, double temperature) {
        logger.info("🔥 Activating heating in {} - Target: 21°C (Current: {}°C)", location, temperature);
        // Integration with heating control systems
    }

    private void updateThermostat(IoTMessage message) {
        logger.debug("🎛️ Updating thermostat settings for {}", message.getLocation());
        // Update smart thermostat based on sensor data
    }

    private void logEnergyUsage(IoTMessage message) {
        logger.debug("⚡ Logging energy usage for HVAC adjustments in {}", message.getLocation());
        // Track energy consumption for analytics
    }
}