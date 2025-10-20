package dev.jkiakumbo.application.dtos

import java.math.BigDecimal

data class CreateCreditCardRequest(
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cvv: String,
    val creditLimit: BigDecimal,
    val userId: String
)

data class ProcessTransactionRequest(
    val cardNumber: String,
    val amount: BigDecimal,
    val currency: String = "GBP",
    val merchant: String,
    val description: String
)