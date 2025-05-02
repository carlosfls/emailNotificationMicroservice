package com.carlosacademic.emailnotificationmicroservice;

import com.carlosacademic.emailnotificationmicroservice.entity.ProcessedEventEntity;
import com.carlosacademic.emailnotificationmicroservice.handler.ProductCreatedEventHandler;
import com.carlosacademic.emailnotificationmicroservice.repositories.ProcessedEventRepository;
import com.carlosacademic.producteventscore.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EmbeddedKafka
@SpringBootTest(properties = "kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
class ProductCreatedEventHandlerIntegrationTest {

    @MockBean
    ProcessedEventRepository processedEventRepository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    ProductCreatedEventHandler productCreatedEventHandler;

    @Test
    void testProductCreatedEventHandler_whenProductCreatedEventReceived_thenProcess() throws Exception {
        //arrange
        String id = UUID.randomUUID().toString();
        String title = "test title";
        int quantity = 1;
        BigDecimal price = BigDecimal.valueOf(100);
        ProductCreatedEvent event = new ProductCreatedEvent(id, title, price, quantity);

        String messageId = UUID.randomUUID().toString();
        String messageKey = event.id();

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                "product-created-events-topic",
                messageKey,
                event
        );

        producerRecord.headers().add("messageId", messageId.getBytes());
        producerRecord.headers().add(KafkaHeaders.RECEIVED_KEY, messageKey.getBytes());

        ProcessedEventEntity eventEntity = new ProcessedEventEntity();

        when(processedEventRepository.findByMessageId(anyString()))
                .thenReturn(Optional.of(eventEntity));

        when(processedEventRepository.save(any(ProcessedEventEntity.class))).thenReturn(eventEntity);
        //act
        kafkaTemplate.send(producerRecord).get();

        //assert
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);

        verify(productCreatedEventHandler, timeout(5000).times(1))
                .handle(eventCaptor.capture(),messageIdCaptor.capture(),messageKeyCaptor.capture());

        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(messageKey, messageKeyCaptor.getValue());
        assertEquals(event, eventCaptor.getValue());
    }
}
