package dev.jkiakumbo.ccm.application.services.application.services

import dev.jkiakumbo.ccm.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.ccm.application.ports.DomainEventPublisher
import dev.jkiakumbo.ccm.application.services.CreditCardServiceImpl
import dev.jkiakumbo.ccm.domain.models.CardStatus
import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.repositories.CreditCardRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class CreditCardServiceTest {

    @Mock
    private lateinit var creditCardRepository: CreditCardRepository

    @Mock
    private lateinit var eventPublisher: DomainEventPublisher

    private lateinit var creditCardService: CreditCardServiceImpl

    @BeforeEach
    fun setUp() {
        creditCardService = CreditCardServiceImpl(
            creditCardRepository,
            eventPublisher
        )
    }

    @Test
    fun `should create credit card successfully`() {
        // Given
        val request = CreateCreditCardRequest(
            cardNumber = "1234567812345678",
            cardHolderName = "John Doe",
            expirationDate = "12/25",
            cvv = "123",
            creditLimit = BigDecimal("1000.00"),
            userId = "user123"
        )

        val savedCard = createCreditCard()

        // Use explicit type parameters with any()
        whenever(creditCardRepository.save(any<CreditCard>())).thenReturn(savedCard)

        // When
        val result = creditCardService.createCreditCard(request)

        // Then
        assertNotNull(result)
        assertEquals(request.cardNumber, result.cardNumber)
        assertEquals(request.cardHolderName, result.cardHolderName)
        assertEquals(request.creditLimit, result.creditLimit)
        verify(creditCardRepository).save(any<CreditCard>())
        verify(eventPublisher).publish(any())
    }


    @Test
    fun `should return null when getting non-existent credit card`() {
        // Given
        val cardId = UUID.randomUUID().toString()
        whenever(creditCardRepository.findById(any())).thenReturn(null)

        // When
        val result = creditCardService.getCreditCard(cardId)

        // Then
        assertNull(result)
        verify(creditCardRepository).findById(argThat { this.value == UUID.fromString(cardId) })
    }

    @Test
    fun `should get credit card successfully when exists`() {
        // Given
        val creditCard = createCreditCard()
        val cardId = creditCard.cardId.value.toString()
        whenever(creditCardRepository.findById(argThat { this.value == UUID.fromString(cardId) })).thenReturn(creditCard)

        // When
        val result = creditCardService.getCreditCard(cardId)

        // Then
        assertNotNull(result)
        assertEquals(creditCard.cardId.value, result?.id)
        assertEquals(creditCard.cardNumber, result?.cardNumber)
        verify(creditCardRepository).findById(argThat { this.value == UUID.fromString(cardId) })
    }

    @Test
    fun `should update credit limit successfully`() {
        // Given
        val creditCard = createCreditCard()
        val cardId = creditCard.cardId.value.toString()
        val newLimit = BigDecimal("2000.00")

        whenever(creditCardRepository.findById(argThat { this.value == UUID.fromString(cardId) })).thenReturn(creditCard)
        whenever(creditCardRepository.update(any<CreditCard>())).thenAnswer { it.arguments[0] as CreditCard }

        // When
        val result = creditCardService.updateCreditLimit(cardId, newLimit)

        // Then
        assertNotNull(result)
        assertEquals(newLimit, result.creditLimit)
        verify(creditCardRepository).findById(argThat { this.value == UUID.fromString(cardId) })
        verify(creditCardRepository).update(any<CreditCard>())
    }

    @Test
    fun `should throw exception when updating non-existent credit card`() {
        // Given
        val cardId = UUID.randomUUID().toString()
        val newLimit = BigDecimal("2000.00")

        whenever(creditCardRepository.findById(any())).thenReturn(null)

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            creditCardService.updateCreditLimit(cardId, newLimit)
        }
        verify(creditCardRepository).findById(any())
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

}