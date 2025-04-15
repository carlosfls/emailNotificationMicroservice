package com.carlosacademic.emailnotificationmicroservice.handler;

import com.carlosacademic.emailnotificationmicroservice.entity.ProcessedEventEntity;
import com.carlosacademic.emailnotificationmicroservice.errors.NonRetryableException;
import com.carlosacademic.emailnotificationmicroservice.repositories.ProcessedEventRepository;
import com.carlosacademic.producteventscore.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@KafkaListener(topics = "product-created-events-topic")
@RequiredArgsConstructor
public class ProductCreatedEventHandler {

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent event,
                       @Header("messageId") String messageId,
                       @Header (KafkaHeaders.RECEIVED_KEY) String messageKey) {
        checkIfMessageWasProcessed(messageId);
        validate(event);
        saveProcessedEvent(messageId, event);
    }

    private void validate(ProductCreatedEvent event) throws NonRetryableException {
        if (event.title().isBlank()) {
            throw new NonRetryableException("Product name is empty!!");
        }
    }

    private void checkIfMessageWasProcessed(String messageId) throws NonRetryableException {
        Optional<ProcessedEventEntity> byMessageId = processedEventRepository.findByMessageId(messageId);
        if (byMessageId.isPresent()) {
            throw new NonRetryableException("Message with id " + messageId + " already processed!");
        }
    }

    private void saveProcessedEvent(String messageId, ProductCreatedEvent event) {
        ProcessedEventEntity processedEvent = new ProcessedEventEntity();
        processedEvent.setMessageId(messageId);
        processedEvent.setProductId(event.id());

        try {
            processedEventRepository.save(processedEvent);
        } catch (DataIntegrityViolationException e) {
            throw new NonRetryableException(e.getMessage());
        }
    }

}
