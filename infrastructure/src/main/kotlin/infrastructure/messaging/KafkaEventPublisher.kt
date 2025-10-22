package dev.jkiakumbo.ccm.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import dev.jkiakumbo.ccm.application.ports.DomainEventPublisher
import dev.jkiakumbo.ccm.domain.events.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : DomainEventPublisher {
    private val logger = LoggerFactory.getLogger(KafkaEventPublisher::class.java)
    override fun publish(event: DomainEvent) {
        try {
            val eventJson = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("credit-card-events", event.aggregateId, eventJson)
            logger.info("Published event: ${event.eventType} for aggregate: ${event.aggregateId}")
        } catch (ex: Exception) {
            logger.error("Failed to publish event: ${event.eventType}", ex)
            throw RuntimeException("Event publishing failed", ex)
        }
    }
}