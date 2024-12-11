package ru.danykor.spring_parprog_demo.listener


import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.danykor.spring_parprog_demo.service.MessageService

@Service
class KafkaListenerService(
    private val messageService: MessageService
) {

    @KafkaListener(topics = ["rs_topic1"], groupId = "group_id")
    fun listen(response: String) {
        val parts = response.split(":")
        if (parts.size == 2) {
            val correlationId = parts[0]
            val responseMessage = parts[1]

            messageService.handleResponse(correlationId, responseMessage)
        }
    }
}
