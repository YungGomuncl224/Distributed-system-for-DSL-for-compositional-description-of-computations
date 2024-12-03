package ru.danykor.spring_parprog_demo.controller

import org.springframework.web.bind.annotation.RestController


import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/messages")
class ExpressionControllerController(private val kafkaTemplate: KafkaTemplate<String, String>) {

    @PostMapping
    fun sendMessage(@RequestBody message: String) {
        kafkaTemplate.send("TestTopic", message)
    }
}
