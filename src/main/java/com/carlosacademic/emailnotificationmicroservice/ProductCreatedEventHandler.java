package com.carlosacademic.emailnotificationmicroservice;

import com.carlosacademic.producteventscore.ProductCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    @KafkaHandler
    public void handle(ProductCreatedEvent event){
        System.out.println("Event received!!! "+ event.title());
    }
}
