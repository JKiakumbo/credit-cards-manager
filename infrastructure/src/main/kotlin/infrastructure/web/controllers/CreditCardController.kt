package dev.jkiakumbo.ccm.infrastructure.web.controllers

import dev.jkiakumbo.ccm.application.dtos.CreateCreditCardRequest
import dev.jkiakumbo.ccm.application.services.CreditCardService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/credit-cards")
class CreditCardController(
    private val creditCardService: CreditCardService
)  {

    @GetMapping("/{cardId}")
    fun getCreditCard(@PathVariable cardId: String): ResponseEntity<Any> {
        return try {
            val creditCard = creditCardService.getCreditCard(cardId)
            creditCard?.let {
                ResponseEntity(it, HttpStatus.OK)
            } ?: ResponseEntity(mapOf("error" to "Credit card not found"), HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping
    fun createCreditCard(@RequestBody request: CreateCreditCardRequest) : ResponseEntity<Any> {
        return try {
            val creditCard = creditCardService.createCreditCard(request)
            ResponseEntity(creditCard, HttpStatus.CREATED)
        } catch (ex: Exception) {
            ResponseEntity(mapOf("Error" to ex.message), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PatchMapping("/{cardId}/credit-limit")
    fun updateCreditLimit(
        @PathVariable cardId: String,
        @RequestBody updateRequest: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val newLimit = (updateRequest["newLimit"] as BigDecimal)
            val creditCard = creditCardService.updateCreditLimit(cardId, newLimit)
            ResponseEntity(creditCard, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }
}


