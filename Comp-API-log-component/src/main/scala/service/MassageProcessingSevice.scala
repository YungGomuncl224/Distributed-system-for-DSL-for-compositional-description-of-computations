package service

import processing.process
import consumer.KafkaConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import producer.KafkaProducerConfig

import java.io.FileInputStream
import scala.jdk.CollectionConverters.*
import java.util
import java.util.{ArrayList, Properties}

object MassageProcessingSevice {

  @main def runApp(name: String): Unit = {
    println("running worker")
    val instanceId = System.getProperty("app.instance.id", "default")
    val i = s"$instanceId"

    println(s" $i")

    val consumer = new KafkaConsumerConfig()
    val producer = new KafkaProducerConfig()

    val topics : util.ArrayList[String] = new util.ArrayList[String]()
    topics.add("10_req_topic")
    consumer.subscribe(topics)
    var c = 0;

    try {
      while (true) {
        val records = consumer.poll(java.time.Duration.ofMillis(100))
        for (record <- records.asScala) {
          println(s"Received message: ${record.value()}")
          val processedMessage = process(record.value())
          //Thread.sleep(100)
          c += 1
          println(c)
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
