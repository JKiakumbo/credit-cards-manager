package dev.jkiakumbo.ccm.domain.repositories

import dev.jkiakumbo.ccm.domain.models.CreditCard
import dev.jkiakumbo.ccm.domain.models.CreditCardId

interface CreditCardRepository {
    fun save(creditCard: CreditCard): CreditCard
    fun findById(id: CreditCardId): CreditCard?
    fun findByCardNumber(number: String): CreditCard?
    fun findByUserId(userId: String): List<CreditCard>
    fun update(creditCard: CreditCard): CreditCard
}