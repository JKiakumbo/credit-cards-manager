package dev.jkiakumbo.domain.events

import java.time.LocalDateTime
import java.util.UUID

class TransactionProcessedEvent (
    override  val eventId: UUID = UUID.randomUUID(),
    val transactionId: String,
    val cardId: String,
    val amount: String,
    val merchant: String,
    val status: String,
    override val aggregateId: String = cardId,
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    override val eventType: String = "TransactionProcessed",
    override val version: Int = 1,
): DomainEvent