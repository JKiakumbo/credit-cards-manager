package dev.jkiakumbo.ccm.infrastructure.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.net.Socket


@Component
class DatabaseHealthIndicator : HealthIndicator {

    override fun health(): Health {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("postgres", 5432), 5000)
            }
            Health.up().build()
        } catch (e: Exception) {
            Health.down().withException(e).build()
        }
    }
}

@Component
class KafkaHealthIndicator : HealthIndicator {

    override fun health(): Health {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("kafka", 9092), 5000)
            }
            Health.up().build()
        } catch (e: Exception) {
            Health.down().withException(e).build()
        }
    }
}