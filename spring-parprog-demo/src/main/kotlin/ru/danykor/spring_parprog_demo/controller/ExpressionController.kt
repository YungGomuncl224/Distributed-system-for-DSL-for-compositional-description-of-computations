package ru.danykor.spring_parprog_demo.controller



import org.springframework.web.bind.annotation.*
import ru.danykor.spring_parprog_demo.service.MessageService

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api/messages")
class ExpressionController(
    private val messageService: MessageService
) {

    @PostMapping
    fun sendMessage(@RequestBody message: String): String? {
        return messageService.sendMessage(message)
    }
}













































//import org.springframework.kafka.annotation.KafkaListener
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.bind.annotation.RestController
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//import java.util.UUID
//
//@RestController
//@CrossOrigin(origins = ["http://localhost:3000"])
//@RequestMapping("/api/messages")
//class ExpressionControllerController(private val kafkaTemplate: KafkaTemplate<String, String>) {
//
//    private val latchMap = mutableMapOf<String, CountDownLatch>()
//    private val responseMap = mutableMapOf<String, String>()
//
//    @PostMapping
//    fun sendMessage(@RequestBody message: String): String? {
//        println("got a message")
//        val correlationId = UUID.randomUUID().toString()
//        val fullMessage = "$correlationId:$message"
//
//        val latch = CountDownLatch(1)
//        latchMap[correlationId] = latch
//
//        kafkaTemplate.send("TestTopic", fullMessage)
//
//        return try {
//            if (latch.await(10, TimeUnit.SECONDS)) {
//                responseMap[correlationId]
//            } else {
//                null // Timeout
//            }
//        } finally {
//            latchMap.remove(correlationId)
//            responseMap.remove(correlationId)
//        }
//    }
//
//    @KafkaListener(topics = ["rs_topic1"], groupId = "group_id")
//    fun listen(response: String) {
//        val parts = response.split(":")
//        if (parts.size == 2) {
//            val correlationId = parts[0]
//            val responseMessage = parts[1]
//
//            responseMap[correlationId] = responseMessage
//
//            latchMap[correlationId]?.countDown()
//        }
//    }
//}

