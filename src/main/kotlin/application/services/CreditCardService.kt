package dev.jkiakumbo.application.services

import dev.jkiakumbo.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.application.dtos.CreditCardResponse
import dev.jkiakumbo.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.application.dtos.TransactionResponse
import dev.jkiakumbo.application.dtos.toResponse
import dev.jkiakumbo.application.ports.DomainEventPublisher
import dev.jkiakumbo.domain.events.CreditCardCreatedEvent
import dev.jkiakumbo.domain.events.TransactionProcessedEvent
import dev.jkiakumbo.domain.models.CardStatus
import dev.jkiakumbo.domain.models.CreditCard
import dev.jkiakumbo.domain.models.CreditCardId
import dev.jkiakumbo.domain.models.InsufficientCreditException
import dev.jkiakumbo.domain.models.Transaction
import dev.jkiakumbo.domain.models.TransactionId
import dev.jkiakumbo.domain.models.TransactionStatus
import dev.jkiakumbo.domain.models.TransactionType
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
        val card = creditCardRepository.findByCardNumber(request.cardNumber)
            ?: throw IllegalArgumentException("Credit card not found")

        val transaction = Transaction(
            id = TransactionId(),
            cardId = card.cardId,
            amount = request.amount,
            currency = request.currency,
            merchant = request.merchant,
            transactionType = TransactionType.PURCHASE,
            status = TransactionStatus.PENDING,
            description = request.description
        )

        return try {
            val updatedCard = card.processPayment(request.amount)
            creditCardRepository.update(updatedCard)

            val approvedTransaction = transaction.copy(status = TransactionStatus.APPROVED)
            val savedTransaction = transactionRepository.save(approvedTransaction)

            eventPublisher.publish(
                TransactionProcessedEvent(
                    transactionId = savedTransaction.id.value.toString(),
                    cardId = card.cardId.value.toString(),
                    amount = savedTransaction.amount.toString(),
                    merchant = savedTransaction.merchant,
                    status = savedTransaction.status.name
                )
            )

            savedTransaction.toResponse()
        } catch (e: InsufficientCreditException) {
            val declinedTransaction = transaction.copy(status = TransactionStatus.DECLINED)
            val savedTransaction = transactionRepository.save(declinedTransaction)
            savedTransaction.toResponse()
        }
    }

    override fun getCardTransactions(cardId: String): List<TransactionResponse> {
        return transactionRepository.findByCardId(CreditCardId(UUID.fromString(cardId)))
            .map { it.toResponse() }
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

