package dev.jkiakumbo.domain.events

import java.time.LocalDateTime
import java.util.UUID

data class CreditCardCreatedEvent(
    override val eventId: UUID,
    val cardId: String,
    val cardNumber: String,
    val creditLimit: String,
    val userId: String,
    override val aggregateId: String = cardId,
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    override val eventType: String = "CreditCardCreated",
    override val version: Int = 1
): DomainEvent
