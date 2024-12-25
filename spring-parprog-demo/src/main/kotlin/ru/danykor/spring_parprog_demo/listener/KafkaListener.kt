package ru.danykor.spring_parprog_demo.listener


import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.danykor.spring_parprog_demo.service.CppService
import ru.danykor.spring_parprog_demo.service.MessageService

@Service
class KafkaListenerService(
    private val messageService: MessageService,
    private val cppService: CppService
) {

    @KafkaListener(topics = ["rs_topic1","TopicB"], groupId = "group_id")
    fun listen(response: String) {
        val parts = response.split(":")
        if (parts.size == 2) {
            val correlationId = parts[0]
            val responseMessage = parts[1]

            messageService.handleResponse(correlationId, responseMessage)
        }
    }

    @KafkaListener(topics = ["TopicB"], groupId = "my_group")
    fun listenCpp(response: String) {
        val parts = response.split(":")
        if (parts.size == 2) {
            val correlationId = parts[0]
            val responseMessage = parts[1]

            cppService.handleResponse(correlationId, responseMessage)
        }
    }
}
