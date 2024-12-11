package ru.danykor.spring_parprog_demo.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.UUID

@Service
class MessageService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    private val latchMap = mutableMapOf<String, CountDownLatch>()
    private val responseMap = mutableMapOf<String, String>()

    fun sendMessage(message: String): String? {
        println("got request")
        val correlationId = UUID.randomUUID().toString()
        val fullMessage = "$correlationId:$message"

        val latch = CountDownLatch(1)
        latchMap[correlationId] = latch

        kafkaTemplate.send("TestTopic", fullMessage)

        return try {
            if (latch.await(10, TimeUnit.SECONDS)) {
                responseMap[correlationId]
            } else {
                null // Timeout
            }
        } finally {
            latchMap.remove(correlationId)
            responseMap.remove(correlationId)
        }
    }

    fun handleResponse(correlationId: String, responseMessage: String) {
        responseMap[correlationId] = responseMessage
        latchMap[correlationId]?.countDown()
    }
}
