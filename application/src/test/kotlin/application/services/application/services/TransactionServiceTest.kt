package dev.jkiakumbo.ccm.application.services.application.services

import dev.jkiakumbo.ccm.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.ccm.application.ports.DomainEventPublisher
import dev.jkiakumbo.ccm.application.services.TransactionService
import dev.jkiakumbo.ccm.application.services.TransactionServiceImpl
import dev.jkiakumbo.ccm.domain.models.CardStatus
import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.models.Transaction
import dev.jkiakumbo.ccm.domain.models.TransactionId
import dev.jkiakumbo.ccm.domain.models.TransactionStatus
import dev.jkiakumbo.ccm.domain.models.TransactionType
import dev.jkiakumbo.ccm.domain.repositories.CreditCardRepository
import dev.jkiakumbo.ccm.domain.repositories.TransactionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {

    @Mock
    private lateinit var creditCardRepository: CreditCardRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var eventPublisher: DomainEventPublisher

    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        transactionService = TransactionServiceImpl(
            creditCardRepository,
            transactionRepository,
            eventPublisher
        )
    }

    @Test
    fun `should process transaction successfully when sufficient credit`() {
        // Given
        val card = createCreditCard()
        val request = ProcessTransactionRequest(
            cardNumber = "1234567812345678",
            amount = BigDecimal("100.00"),
            merchant = "Test Store",
            description = "Test purchase"
        )

        whenever(creditCardRepository.findByCardNumber(eq("1234567812345678"))).thenReturn(card)
        whenever(creditCardRepository.update(any<CreditCard>())).thenAnswer { it.arguments[0] as CreditCard }
        whenever(transactionRepository.save(any<Transaction>())).thenAnswer { it.arguments[0] as Transaction }

        // When
        val result = transactionService.processTransaction(request)

        // Then
        assertEquals("APPROVED", result.status)
        assertEquals(request.amount, result.amount)
        assertEquals(request.merchant, result.merchant)
        verify(creditCardRepository).update(any<CreditCard>())
        verify(transactionRepository).save(any<Transaction>())
        verify(eventPublisher).publish(any())
    }

    @Test
    fun `should decline transaction when insufficient credit`() {
        // Given
        val card = createCreditCard()
        val request = ProcessTransactionRequest(
            cardNumber = "1234567812345678",
            amount = BigDecimal("1500.00"), // More than credit limit
            merchant = "Test Store",
            description = "Test purchase"
        )

        whenever(creditCardRepository.findByCardNumber(eq("1234567812345678"))).thenReturn(card)
        whenever(transactionRepository.save(any<Transaction>())).thenAnswer { it.arguments[0] as Transaction }

        // When
        val result = transactionService.processTransaction(request)

        // Then
        assertEquals("DECLINED", result.status)
        verify(creditCardRepository, org.mockito.kotlin.never()).update(any<CreditCard>())
        verify(transactionRepository).save(any<Transaction>())
    }

    @Test
    fun `should get empty transactions list for card with no transactions`() {
        // Given
        val cardId = UUID.randomUUID().toString()
        whenever(transactionRepository.findByCardId(any())).thenReturn(emptyList())

        // When
        val result = transactionService.getCardTransactions(cardId)

        // Then
        assertTrue(result.isEmpty())
        verify(transactionRepository).findByCardId(argThat { this.value == UUID.fromString(cardId) })
    }

    @Test
    fun `should get transactions list for card with transactions`() {
        // Given
        val creditCard = createCreditCard()
        val cardId = creditCard.cardId.value.toString()
        val transactions = listOf(
            createTransaction(creditCard.cardId, BigDecimal("100.00")),
            createTransaction(creditCard.cardId, BigDecimal("50.00"))
        )

        whenever(transactionRepository.findByCardId(argThat { this.value == UUID.fromString(cardId) })).thenReturn(transactions)

        // When
        val result = transactionService.getCardTransactions(cardId)

        // Then
        assertEquals(2, result.size)
        verify(transactionRepository).findByCardId(argThat { this.value == UUID.fromString(cardId) })
    }

    @Test
    fun `should throw exception when processing transaction for non-existent card`() {
        // Given
        val request = ProcessTransactionRequest(
            cardNumber = "9999999999999999",
            amount = BigDecimal("100.00"),
            merchant = "Test Store",
            description = "Test purchase"
        )

        whenever(creditCardRepository.findByCardNumber(eq("9999999999999999"))).thenReturn(null)

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            transactionService.processTransaction(request)
        }

        verify(creditCardRepository).findByCardNumber(eq("9999999999999999"))
        verify(transactionRepository, org.mockito.kotlin.never()).save(any<Transaction>())
        verify(creditCardRepository, org.mockito.kotlin.never()).update(any<CreditCard>())
    }

    private fun createCreditCard(): CreditCard {
        return CreditCard(
            cardId = CreditCardId(UUID.randomUUID()),
            cardNumber = "1234567812345678",
            cardHolderName = "John Doe",
            expirationDate = "12/25",
            cvv = "123",
            creditLimit = BigDecimal("1000.00"),
            availableCredit = BigDecimal("1000.00"),
            currentBalance = BigDecimal.ZERO,
            userId = "user123",
            status = CardStatus.ACTIVE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    private fun createTransaction(cardId: CreditCardId, amount: BigDecimal): Transaction {
        return Transaction(
            id = TransactionId(UUID.randomUUID()),
            cardId = cardId,
            amount = amount,
            currency = "USD",
            merchant = "Test Merchant",
            transactionType = TransactionType.PURCHASE,
            status = TransactionStatus.APPROVED,
            description = "Test transaction",
            timestamp = LocalDateTime.now()
        )
    }
}