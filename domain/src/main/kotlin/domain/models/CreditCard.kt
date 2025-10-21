package dev.jkiakumbo.ccm.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreditCardId(val value: UUID = UUID.randomUUID())

data class CreditCard(
    val cardId: CreditCardId,
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cvv: String,
    val creditLimit: BigDecimal,
    val availableCredit: BigDecimal,
    val currentBalance: BigDecimal,
    val userId: String,
    val status: CardStatus = CardStatus.INACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun authorizePayment(amount: BigDecimal): Boolean {
        return availableCredit >= amount && status == CardStatus.ACTIVE
    }

    fun processPayment(amount: BigDecimal): CreditCard {
        if (!authorizePayment(amount)) {
            throw InsufficientCreditException("Insufficient credit for payment")
        }

        return copy(
            availableCredit = availableCredit.subtract(amount),
            currentBalance = currentBalance.add(amount),
            updatedAt = LocalDateTime.now()
        )
    }

    fun processRefund(amount: BigDecimal): CreditCard {
        return copy(
            availableCredit = availableCredit.add(amount),
            currentBalance = currentBalance.subtract(amount),
            updatedAt = LocalDateTime.now()
        )
    }

}

enum class CardStatus {
    ACTIVE, INACTIVE, BLOCKED, EXPIRED
}

class InsufficientCreditException(message: String) : RuntimeException(message)