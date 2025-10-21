package dev.jkiakumbo.ccm.application.ports

import dev.jkiakumbo.domain.events.DomainEvent

interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}