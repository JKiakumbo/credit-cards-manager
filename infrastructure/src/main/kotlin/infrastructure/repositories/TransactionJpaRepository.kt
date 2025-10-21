package dev.jkiakumbo.ccm.infrastructure.repositories

import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.models.Transaction
import dev.jkiakumbo.ccm.domain.models.TransactionId
import dev.jkiakumbo.ccm.domain.models.TransactionStatus
import dev.jkiakumbo.ccm.domain.models.TransactionType
import dev.jkiakumbo.ccm.domain.repositories.TransactionRepository
import dev.jkiakumbo.ccm.infrastructure.entities.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

interface TransactionJpaRepository : JpaRepository<TransactionEntity, UUID> {
    fun findByCardId(cardId: UUID): List<TransactionEntity>
    fun findByCardIdAndTimestampBetween(cardId: UUID, start: LocalDateTime, end: LocalDateTime): List<TransactionEntity>
}

@Repository
class TransactionRepositoryImpl(
    private val transactionJpaRepository: TransactionJpaRepository
) : TransactionRepository {

    override fun save(transaction: Transaction): Transaction {
        val entity = transaction.toEntity()
        return transactionJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: TransactionId): Transaction? {
        return transactionJpaRepository.findById(id.value).orElse(null)?.toDomain()
    }

    override fun findByCardId(cardId: CreditCardId): List<Transaction> {
        return transactionJpaRepository.findByCardId(cardId.value).map { it.toDomain() }
    }

    override fun findTransactionsBetweenDates(cardId: CreditCardId, start: LocalDateTime, end: LocalDateTime): List<Transaction> {
        return transactionJpaRepository.findByCardIdAndTimestampBetween(cardId.value, start, end).map { it.toDomain() }
    }
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id.value,
        cardId = cardId.value,
        amount = amount,
        currency = currency,
        merchant = merchant,
        transactionType = TransactionType.valueOf(transactionType.name),
        status = TransactionStatus.valueOf(status.name),
        description = description,
        timestamp = timestamp
    )
}

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = TransactionId(id),
        cardId = CreditCardId(cardId),
        amount = amount,
        currency = currency,
        merchant = merchant,
        transactionType = TransactionType.valueOf(transactionType.name),
        status = TransactionStatus.valueOf(status.name),
        description = description,
        timestamp = timestamp
    )
}