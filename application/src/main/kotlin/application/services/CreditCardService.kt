package dev.jkiakumbo.ccm.application.services


import dev.jkiakumbo.ccm.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.ccm.application.dtos.CreditCardResponse
import dev.jkiakumbo.ccm.application.dtos.toResponse
import dev.jkiakumbo.ccm.application.ports.DomainEventPublisher
import dev.jkiakumbo.ccm.domain.events.CreditCardCreatedEvent
import dev.jkiakumbo.ccm.domain.models.CardStatus
import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.repositories.CreditCardRepository
import java.math.BigDecimal
import java.util.UUID

interface CreditCardService {
    fun createCreditCard(request: CreateCreditCardRequest): CreditCardResponse
    fun getCreditCard(cardId: String): CreditCardResponse?
    fun updateCreditLimit(cardId: String, limit: BigDecimal): CreditCardResponse
}

class CreditCardServiceImpl(
    private val creditCardRepository: CreditCardRepository,
    private val eventPublisher: DomainEventPublisher,
) : CreditCardService {
    override fun createCreditCard(request: CreateCreditCardRequest): CreditCardResponse {
        val creditCard = CreditCard(
            cardId = CreditCardId(),
            cardNumber = request.cardNumber,
            cardHolderName = request.cardHolderName,
            expirationDate = request.expirationDate,
            cvv = request.cvv,
            creditLimit = request.creditLimit,
            availableCredit = request.creditLimit,
            currentBalance = BigDecimal.ZERO,
            userId = request.userId,
            status = CardStatus.ACTIVE,
        )

        val savedCreditCard = creditCardRepository.save(creditCard)
        eventPublisher.publish(
            CreditCardCreatedEvent(
                eventId = UUID.randomUUID(),
                cardId = savedCreditCard.cardId.value.toString(),
                cardNumber = savedCreditCard.cardNumber,
                creditLimit = savedCreditCard.creditLimit.toString(),
                userId = savedCreditCard.userId
            )
        )

        return savedCreditCard.toResponse()
    }

    override fun getCreditCard(cardId: String): CreditCardResponse? {
        return creditCardRepository.findById(CreditCardId(UUID.fromString(cardId)))?.toResponse()
    }

    override fun updateCreditLimit(cardId: String, limit: BigDecimal): CreditCardResponse {
        val card = creditCardRepository.findById(CreditCardId(UUID.fromString(cardId)))
            ?: throw IllegalArgumentException("Credit card not found")

        val updatedCard = card.copy(
            creditLimit = limit,
            availableCredit = limit.subtract(card.currentBalance)
        )

        return creditCardRepository.update(updatedCard).toResponse()
    }

}

