package dev.jkiakumbo.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class TransactionId(val value: UUID = UUID.randomUUID())
data class Transaction(
    val id: TransactionId,
    val cardId: CreditCardId,
    val amount: BigDecimal,
    val currency: String,
    val merchant: String,
    val transactionType: TransactionType,
    val status: TransactionStatus,
    val description: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    PURCHASE, REFUND, CASH_ADVANCE, FEE
}

enum class TransactionStatus {
    PENDING, APPROVED, DECLINED, SETTLED
}