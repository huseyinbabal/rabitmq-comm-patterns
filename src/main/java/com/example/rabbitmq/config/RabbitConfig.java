package com.example.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);
        return template;
    }

    // Point-to-Point Pattern - Order Processing
    public static final String ORDER_QUEUE = "order.processing.queue";

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    // Publish/Subscribe Pattern - Social Media Notifications
    public static final String SOCIAL_FANOUT_EXCHANGE = "social.fanout.exchange";
    public static final String TIMELINE_QUEUE = "social.timeline.queue";
    public static final String NOTIFICATION_QUEUE = "social.notification.queue";
    public static final String ANALYTICS_QUEUE = "social.analytics.queue";

    @Bean
    public FanoutExchange socialFanoutExchange() {
        return new FanoutExchange(SOCIAL_FANOUT_EXCHANGE);
    }

    @Bean
    public Queue timelineQueue() {
        return QueueBuilder.durable(TIMELINE_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(ANALYTICS_QUEUE).build();
    }

    @Bean
    public Binding timelineBinding() {
        return BindingBuilder.bind(timelineQueue()).to(socialFanoutExchange());
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(socialFanoutExchange());
    }

    @Bean
    public Binding analyticsBinding() {
        return BindingBuilder.bind(analyticsQueue()).to(socialFanoutExchange());
    }

    // Direct Exchange Pattern - Log Processing
    public static final String LOG_DIRECT_EXCHANGE = "log.direct.exchange";
    public static final String ERROR_LOG_QUEUE = "log.error.queue";
    public static final String WARNING_LOG_QUEUE = "log.warning.queue";
    public static final String INFO_LOG_QUEUE = "log.info.queue";

    @Bean
    public DirectExchange logDirectExchange() {
        return new DirectExchange(LOG_DIRECT_EXCHANGE);
    }

    @Bean
    public Queue errorLogQueue() {
        return QueueBuilder.durable(ERROR_LOG_QUEUE).build();
    }

    @Bean
    public Queue warningLogQueue() {
        return QueueBuilder.durable(WARNING_LOG_QUEUE).build();
    }

    @Bean
    public Queue infoLogQueue() {
        return QueueBuilder.durable(INFO_LOG_QUEUE).build();
    }

    @Bean
    public Binding errorLogBinding() {
        return BindingBuilder.bind(errorLogQueue()).to(logDirectExchange()).with("error");
    }

    @Bean
    public Binding warningLogBinding() {
        return BindingBuilder.bind(warningLogQueue()).to(logDirectExchange()).with("warning");
    }

    @Bean
    public Binding infoLogBinding() {
        return BindingBuilder.bind(infoLogQueue()).to(logDirectExchange()).with("info");
    }

    // Topic Exchange Pattern - IoT Device Management
    public static final String IOT_TOPIC_EXCHANGE = "iot.topic.exchange";
    public static final String HVAC_QUEUE = "iot.hvac.queue";
    public static final String SECURITY_QUEUE = "iot.security.queue";
    public static final String BATTERY_QUEUE = "iot.battery.queue";
    public static final String IOT_ANALYTICS_QUEUE = "iot.analytics.queue";

    @Bean
    public TopicExchange iotTopicExchange() {
        return new TopicExchange(IOT_TOPIC_EXCHANGE);
    }

    @Bean
    public Queue hvacQueue() {
        return QueueBuilder.durable(HVAC_QUEUE).build();
    }

    @Bean
    public Queue securityQueue() {
        return QueueBuilder.durable(SECURITY_QUEUE).build();
    }

    @Bean
    public Queue batteryQueue() {
        return QueueBuilder.durable(BATTERY_QUEUE).build();
    }

    @Bean
    public Queue iotAnalyticsQueue() {
        return QueueBuilder.durable(IOT_ANALYTICS_QUEUE).build();
    }

    @Bean
    public Binding hvacBinding() {
        return BindingBuilder.bind(hvacQueue()).to(iotTopicExchange()).with("sensor.temperature.*");
    }

    @Bean
    public Binding securityBinding() {
        return BindingBuilder.bind(securityQueue()).to(iotTopicExchange()).with("sensor.motion.*");
    }

    @Bean
    public Binding batteryBinding() {
        return BindingBuilder.bind(batteryQueue()).to(iotTopicExchange()).with("device.*.battery");
    }

    @Bean
    public Binding iotAnalyticsBinding() {
        return BindingBuilder.bind(iotAnalyticsQueue()).to(iotTopicExchange()).with("sensor.#");
    }

    // Work Queue Pattern - Image Processing
    public static final String IMAGE_PROCESSING_QUEUE = "image.processing.queue";

    @Bean
    public Queue imageProcessingQueue() {
        return QueueBuilder.durable(IMAGE_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-message-ttl", 300000) // 5 minutes TTL
                .build();
    }

    // Dead Letter Exchange for error handling
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_QUEUE = "dlx.queue";

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("dlx");
    }
}