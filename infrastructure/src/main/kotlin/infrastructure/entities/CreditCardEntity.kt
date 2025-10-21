package dev.jkiakumbo.ccm.infrastructure.entities

import dev.jkiakumbo.ccm.domain.models.CardStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "credit_cards")
data class CreditCardEntity(
    @Id
    val id: UUID,

    @Column(name = "card_number", unique = true, nullable = false)
    val cardNumber: String,

    @Column(name = "card_holder_name", nullable = false)
    val cardHolderName: String,

    @Column(name = "expiration_date", nullable = false)
    val expirationDate: String,

    @Column(name = "cvv", nullable = false)
    val cvv: String,

    @Column(name = "credit_limit", precision = 19, scale = 4, nullable = false)
    val creditLimit: BigDecimal,

    @Column(name = "available_credit", precision = 19, scale = 4, nullable = false)
    val availableCredit: BigDecimal,

    @Column(name = "current_balance", precision = 19, scale = 4, nullable = false)
    val currentBalance: BigDecimal,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: CardStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime
)


