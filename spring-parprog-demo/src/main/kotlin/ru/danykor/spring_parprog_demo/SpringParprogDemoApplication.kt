package ru.danykor.spring_parprog_demo

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.Properties

@SpringBootApplication
@EnableAdminServer
class SpringParprogDemoApplication

fun main(args: Array<String>) {
	runApplication<SpringParprogDemoApplication>(*args)
	println("I m here")

//	val props = Properties()
//	props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//	val producer = KafkaProducer<String,String>(props)
}
