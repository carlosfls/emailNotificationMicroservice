package com.carlosacademic.emailnotificationmicroservice.handler;

import com.carlosacademic.producteventscore.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@KafkaListener(topics = "product-created-events-topic.DLT")
public class ProductCreatedEventErrorHandler {

    private final Logger LOG = LoggerFactory.getLogger(ProductCreatedEventErrorHandler.class);

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent event,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        LOG.info("Message id {}", messageId);
        LOG.info("Message key {}", messageKey);
        LOG.info("Processing the failed event {}", event);
    }
}
