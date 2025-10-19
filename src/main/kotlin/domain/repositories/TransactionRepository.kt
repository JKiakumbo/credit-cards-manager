package dev.jkiakumbo.domain.repositories

import dev.jkiakumbo.domain.models.CreditCardId
import dev.jkiakumbo.domain.models.Transaction
import dev.jkiakumbo.domain.models.TransactionId
import java.time.LocalDateTime

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction
    fun findById(id: TransactionId): Transaction?
    fun findByCardId(cardId: CreditCardId): List<Transaction>
    fun findTransactionsBetweenDates(cardId: CreditCardId, from: LocalDateTime, to: LocalDateTime): List<Transaction>
}