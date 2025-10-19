package dev.jkiakumbo.domain.events

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

interface DomainEvent {
    val eventId: UUID
    val aggregateId: String
    val occurredOn: LocalDateTime
    val eventType: String
    val version: Int
}
