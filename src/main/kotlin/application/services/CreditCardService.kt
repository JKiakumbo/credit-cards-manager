package dev.jkiakumbo.application.services

import dev.jkiakumbo.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.application.dtos.CreditCardResponse
import dev.jkiakumbo.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.application.dtos.TransactionResponse
import dev.jkiakumbo.application.dtos.toResponse
import dev.jkiakumbo.application.ports.DomainEventPublisher
import dev.jkiakumbo.domain.events.CreditCardCreatedEvent
import dev.jkiakumbo.domain.models.CardStatus
import dev.jkiakumbo.domain.models.CreditCard
import dev.jkiakumbo.domain.models.CreditCardId
import dev.jkiakumbo.domain.repositories.CreditCardRepository
import dev.jkiakumbo.domain.repositories.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

interface CreditCardService {
    fun createCreditCard(request: CreateCreditCardRequest): CreditCardResponse
    fun getCreditCard(cardId: String): CreditCardResponse?
    fun processTransaction(request: ProcessTransactionRequest): TransactionResponse
    fun getCardTransactions(cardId: String): List<TransactionResponse>
    fun updateCreditLimit(cardId: String, limit: BigDecimal): CreditCardResponse
}

class CreditCardServiceImpl(
    private val creditCardRepository: CreditCardRepository,
    private val transactionRepository: TransactionRepository,
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

    override fun processTransaction(request: ProcessTransactionRequest): TransactionResponse {
        TODO("Not yet implemented")
    }

    override fun getCardTransactions(cardId: String): List<TransactionResponse> {
        TODO("Not yet implemented")
    }

    override fun updateCreditLimit(
        cardId: String,
        limit: BigDecimal
    ): CreditCardResponse {
        TODO("Not yet implemented")
    }

}

