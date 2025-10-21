package dev.jkiakumbo.ccm.application.dtos


import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreditCardResponse(
    val id: UUID,
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val creditLimit: BigDecimal,
    val availableCredit: BigDecimal,
    val currentBalance: BigDecimal,
    val userId: String,
    val status: String,
    val createdAt: LocalDateTime
)

data class TransactionResponse(
    val id: UUID,
    val cardId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val merchant: String,
    val transactionType: String,
    val status: String,
    val description: String,
    val timestamp: LocalDateTime
)

fun CreditCard.toResponse(): CreditCardResponse {
    return CreditCardResponse(
        id = cardId.value,
        cardNumber = cardNumber,
        cardHolderName = cardHolderName,
        expirationDate = expirationDate,
        creditLimit = creditLimit,
        availableCredit = availableCredit,
        currentBalance = currentBalance,
        userId = userId,
        status = status.name,
        createdAt = createdAt
    )
}

fun Transaction.toResponse(): TransactionResponse {
    return TransactionResponse(
        id = id.value,
        cardId = cardId.value,
        amount = amount,
        currency = currency,
        merchant = merchant,
        transactionType = transactionType.name,
        status = status.name,
        description = description,
        timestamp = timestamp
    )
}