package dev.jkiakumbo.ccm.application.services


import dev.jkiakumbo.ccm.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.ccm.application.dtos.CreditCardResponse
import dev.jkiakumbo.ccm.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.ccm.application.dtos.TransactionResponse
import dev.jkiakumbo.ccm.application.dtos.toResponse
import dev.jkiakumbo.ccm.application.ports.DomainEventPublisher
import dev.jkiakumbo.ccm.domain.events.CreditCardCreatedEvent
import dev.jkiakumbo.ccm.domain.events.TransactionProcessedEvent
import dev.jkiakumbo.ccm.domain.models.CardStatus
import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.models.InsufficientCreditException
import dev.jkiakumbo.ccm.domain.models.Transaction
import dev.jkiakumbo.ccm.domain.models.TransactionId
import dev.jkiakumbo.ccm.domain.models.TransactionStatus
import dev.jkiakumbo.ccm.domain.models.TransactionType
import dev.jkiakumbo.ccm.domain.repositories.CreditCardRepository
import dev.jkiakumbo.ccm.domain.repositories.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

interface TransactionService {
    fun processTransaction(request: ProcessTransactionRequest): TransactionResponse
    fun getCardTransactions(cardId: String): List<TransactionResponse>
}

class TransactionServiceImpl(
    private val creditCardRepository: CreditCardRepository,
    private val transactionRepository: TransactionRepository,
    private val eventPublisher: DomainEventPublisher,
) : TransactionService {


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

}

