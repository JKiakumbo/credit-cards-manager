package dev.jkiakumbo.ccm.domain.models


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CreditCardTest {

    @Test
    fun `should authorize payment when sufficient credit available`() {
        val creditCard = createCreditCard(creditLimit = BigDecimal("1000.00"))

        val canAuthorize = creditCard.authorizePayment(BigDecimal("500.00"))

        assertTrue(canAuthorize)
    }

    @Test
    fun `should not authorize payment when insufficient credit`() {
        val creditCard = createCreditCard(creditLimit = BigDecimal("100.00"))

        val canAuthorize = creditCard.authorizePayment(BigDecimal("200.00"))

        assertFalse(canAuthorize)
    }

    @Test
    fun `should process payment successfully`() {
        val creditCard = createCreditCard(creditLimit = BigDecimal("1000.00"))

        val updatedCard = creditCard.processPayment(BigDecimal("300.00"))

        assertEquals(BigDecimal("700.00"), updatedCard.availableCredit)
        assertEquals(BigDecimal("300.00"), updatedCard.currentBalance)
    }

    @Test
    fun `should throw exception when processing payment with insufficient credit`() {
        val creditCard = createCreditCard(creditLimit = BigDecimal("100.00"))

        assertThrows(InsufficientCreditException::class.java) {
            creditCard.processPayment(BigDecimal("200.00"))
        }
    }

    private fun createCreditCard(creditLimit: BigDecimal): CreditCard {
        return CreditCard(
            cardId = CreditCardId(),
            cardNumber = "1234567812345678",
            cardHolderName = "John Doe",
            expirationDate = "12/25",
            cvv = "123",
            creditLimit = creditLimit,
            availableCredit = creditLimit,
            currentBalance = BigDecimal.ZERO,
            userId = "user123",
            status = CardStatus.ACTIVE
        )
    }
}