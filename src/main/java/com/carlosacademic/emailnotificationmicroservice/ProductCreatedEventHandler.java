package com.carlosacademic.emailnotificationmicroservice;

import com.carlosacademic.emailnotificationmicroservice.errors.NonRetryableException;
import com.carlosacademic.producteventscore.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent event,
                       @Header(name = "messageId") String messageId) {
        log.info("Message received!!!");
        validate(event);
        log.info("Received messageId: {}, content {}",messageId, event);
    }

    private void validate(ProductCreatedEvent event) throws NonRetryableException {
        if (event.title().isBlank()) {
            throw new NonRetryableException("Product name is empty!!");
        }
    }

}
