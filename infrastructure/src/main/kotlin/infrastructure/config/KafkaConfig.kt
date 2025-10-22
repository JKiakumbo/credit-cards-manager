package dev.jkiakumbo.ccm.infrastructure.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        return KafkaAdmin(configs)
    }

    @Bean
    fun creditCardEventsTopic(): NewTopic {
        return NewTopic("credit-card-events", 3, 1.toShort())
    }

    @Bean
    fun transactionEventsTopic(): NewTopic {
        return NewTopic("transaction-events", 3, 1.toShort())
    }
}