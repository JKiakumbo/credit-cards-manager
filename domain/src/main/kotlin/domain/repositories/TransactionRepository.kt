package dev.jkiakumbo.ccm.domain.repositories

import dev.jkiakumbo.ccm.domain.models.CreditCardId
import dev.jkiakumbo.ccm.domain.models.Transaction
import dev.jkiakumbo.ccm.domain.models.TransactionId
import java.time.LocalDateTime

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction
    fun findById(id: TransactionId): Transaction?
    fun findByCardId(cardId: CreditCardId): List<Transaction>
    fun findTransactionsBetweenDates(cardId: CreditCardId, from: LocalDateTime, to: LocalDateTime): List<Transaction>
}