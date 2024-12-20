package ru.danykor.spring_parprog_demo.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener

//@Configuration
//@EnableKafka
//class KafkaConfig {
//
//    @Bean
//    fun producerFactory(): ProducerFactory<String, String> {
//        val configProps = mutableMapOf<String, Any>()
//        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        return DefaultKafkaProducerFactory(configProps)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, String> {
//        return KafkaTemplate(producerFactory())
//    }
//}
