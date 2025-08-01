package com.example.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.ErrorHandler;

@Configuration
public class ErrorHandlingConfig implements RabbitListenerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfig.class);

    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(customFatalExceptionStrategy());
    }

    @Bean
    public FatalExceptionStrategy customFatalExceptionStrategy() {
        return new CustomFatalExceptionStrategy();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
        return retryTemplate;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, RabbitConfig.DLX_EXCHANGE, "dlx");
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        // Additional configuration can be added here
    }

    public static class CustomFatalExceptionStrategy implements FatalExceptionStrategy {
        private static final Logger logger = LoggerFactory.getLogger(CustomFatalExceptionStrategy.class);

        @Override
        public boolean isFatal(Throwable t) {
            logger.error("Evaluating if exception is fatal: {}", t.getClass().getSimpleName());
            
            // Don't retry for these fatal exceptions
            return t instanceof IllegalArgumentException || 
                   t instanceof SecurityException ||
                   t instanceof ClassCastException;
        }
    }
}