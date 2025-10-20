package dev.jkiakumbo.application.services

import dev.jkiakumbo.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.application.dtos.CreditCardResponse
import dev.jkiakumbo.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.application.dtos.TransactionResponse
import java.math.BigDecimal

interface CreditCardService {
    fun createCreditCard(request: CreateCreditCardRequest): CreditCardResponse
    fun getCreditCard(cardId: String): CreditCardResponse?
    fun processTransaction(request: ProcessTransactionRequest): TransactionResponse
    fun getCardTransactions(cardId: String): List<TransactionResponse>
    fun updateCreditLimit(cardId: String, limit: BigDecimal): CreditCardResponse
}

