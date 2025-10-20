package dev.jkiakumbo.application.dtos

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