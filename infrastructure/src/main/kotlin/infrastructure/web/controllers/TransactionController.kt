package dev.jkiakumbo.ccm.infrastructure.web.controllers

import dev.jkiakumbo.ccm.application.dtos.ProcessTransactionRequest
import dev.jkiakumbo.ccm.application.services.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @GetMapping("/{cardId}")
    fun getCardTransactions(@PathVariable cardId: String): ResponseEntity<Any> {
        return try {
            val transactions = transactionService.getCardTransactions(cardId)
            ResponseEntity(transactions, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping
    fun processTransaction(@RequestBody request: ProcessTransactionRequest): ResponseEntity<Any> {
        return try {
            val transaction = transactionService.processTransaction(request)
            ResponseEntity(transaction, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }


}