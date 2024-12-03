package service

import processing.process
import consumer.KafkaConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import producer.KafkaProducerConfig
import scala.jdk.CollectionConverters._

import java.util
import java.util.ArrayList

object MassageProcessingSevice {

  @main def runApp(name: String): Unit = {
    val consumer = new KafkaConsumerConfig()
    val producer = new KafkaProducerConfig()

    val topics : util.ArrayList[String] = new util.ArrayList[String]()
    topics.add("TestTopic")
    consumer.subscribe(topics)

    try {
      while (true) {
        val records = consumer.poll(java.time.Duration.ofMillis(100))
        for (record <- records.asScala) {
          println(s"Received message: ${record.value()}")
          val processedMessage = process(record.value())
          producer.send(processedMessage)
        }
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      consumer.close()
      producer.close()
    }
  }
}
