package dev.jkiakumbo.domain.repositories

import dev.jkiakumbo.domain.models.CreditCard
import dev.jkiakumbo.domain.models.CreditCardId

interface CreditCardRepository {
    fun save(creditCard: CreditCard): CreditCard
    fun findById(id: CreditCardId): CreditCard?
    fun findByCardNumber(number: String): CreditCard?
    fun findByUserId(userId: String): List<CreditCard>
    fun update(creditCard: CreditCard): CreditCard
}