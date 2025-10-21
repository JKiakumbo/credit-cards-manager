package dev.jkiakumbo.ccm.infrastructure.repositories

import dev.jkiakumbo.ccm.domain.models.CardStatus
import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.repositories.CreditCardRepository
import dev.jkiakumbo.ccm.infrastructure.entities.CreditCardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

interface CreditCardJpaRepository : JpaRepository<CreditCardEntity, UUID> {
    fun findByCardNumber(cardNumber: String): CreditCardEntity?
    fun findByUserId(userId: String): List<CreditCardEntity>
}

@Repository
class CreditCardRepositoryImpl(
    private val creditCardJpaRepository: CreditCardJpaRepository
) : CreditCardRepository {

    override fun save(creditCard: CreditCard): CreditCard {
        val entity = creditCard.toEntity()
        return creditCardJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: CreditCardId): CreditCard? {
        return creditCardJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByCardNumber(cardNumber: String): CreditCard? {
        return creditCardJpaRepository.findByCardNumber(cardNumber)?.toDomain()
    }

    override fun findByUserId(userId: String): List<CreditCard> {
        return creditCardJpaRepository.findByUserId(userId).map { it.toDomain() }
    }

    override fun update(creditCard: CreditCard): CreditCard {
        return save(creditCard)
    }
}

fun CreditCard.toEntity(): CreditCardEntity {
    return CreditCardEntity(
        id = cardId.value,
        cardNumber = cardNumber,
        cardHolderName = cardHolderName,
        expirationDate = expirationDate,
        cvv = cvv,
        creditLimit = creditLimit,
        availableCredit = availableCredit,
        currentBalance = currentBalance,
        userId = userId,
        status = CardStatus.valueOf(status.name),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun CreditCardEntity.toDomain(): CreditCard {
    return CreditCard(
        cardId = CreditCardId(id),
        cardNumber = cardNumber,
        cardHolderName = cardHolderName,
        expirationDate = expirationDate,
        cvv = cvv,
        creditLimit = creditLimit,
        availableCredit = availableCredit,
        currentBalance = currentBalance,
        userId = userId,
        status = CardStatus.valueOf(status.name),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
