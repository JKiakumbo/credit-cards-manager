package dev.jkiakumbo.ccm.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import dev.jkiakumbo.ccm.domain.events.CreditCardCreatedEvent
import dev.jkiakumbo.ccm.domain.events.TransactionProcessedEvent
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event

class KafkaEventConsumer(
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(KafkaEventConsumer::class.java)

    fun consumeEvent(message: String) {
        try {
            val eventNode = objectMapper.readTree(message)
            val eventType = eventNode.get("eventType").asText()

            when (eventType) {
                "CreditCardCreated" -> {
                    val event = objectMapper.readValue(message, CreditCardCreatedEvent::class.java)
                    handleCreditCardCreated(event)
                }
                "TransactionProcessed" -> {
                    val event = objectMapper.readValue(message, TransactionProcessedEvent::class.java)
                    handleTransactionProcessed(event)
                }
            }
        } catch (ex: Exception) {
            logger.error("Failed to process event: $message", ex)
        }
    }

    private fun handleCreditCardCreated(event: CreditCardCreatedEvent) {
        logger.info("Processing CreditCardCreated event: Card ${event.cardNumber} for user ${event.userId}")
        // Here you can add additional processing like:
        // - Send welcome email
        // - Update search indexes
        // - Notify other services
    }
    private fun handleTransactionProcessed(event: TransactionProcessedEvent) {
        logger.info("Processing TransactionProcessed event: ${event.transactionId} for card ${event.cardId}")
        // Here you can add additional processing like:
        // - Fraud detection
        // - Reward points calculation
        // - Notifications
    }
}