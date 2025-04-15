package com.carlosacademic.emailnotificationmicroservice.repositories;

import com.carlosacademic.emailnotificationmicroservice.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long> {

    Optional<ProcessedEventEntity> findByProductId(String productId);

    Optional<ProcessedEventEntity> findByMessageId(String messageId);
}