package dev.jkiakumbo.ccm.infrastructure.entities

import dev.jkiakumbo.ccm.domain.models.TransactionStatus
import dev.jkiakumbo.ccm.domain.models.TransactionType
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
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    val id: UUID,

    @Column(name = "card_id", nullable = false)
    val cardId: UUID,

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    val amount: BigDecimal,

    @Column(name = "currency", nullable = false)
    val currency: String,

    @Column(name = "merchant", nullable = false)
    val merchant: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: TransactionStatus,

    @Column(name = "description")
    val description: String,

    @Column(name = "timestamp", nullable = false)
    val timestamp: LocalDateTime
)


