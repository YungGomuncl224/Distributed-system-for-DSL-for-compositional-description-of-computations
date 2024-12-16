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

    //    val properties = new Properties()
//    val inputStream = new FileInputStream("build.properties")
//
//    try {
//      properties.load(inputStream)
//
//      // Получите значение порта с использованием значения по умолчанию
//      val appPort = properties.getProperty("app.port", "8080")
//
//      // Выводим номер порта на экран
//      println(s"Application port: $appPort")
//    } catch {
//      case e: Exception => e.printStackTrace()
//    } finally {
//      inputStream.close()
//    }
    //println(s"Starting application on port: $port")
    val consumer = new KafkaConsumerConfig()
    val producer = new KafkaProducerConfig()

    val topics : util.ArrayList[String] = new util.ArrayList[String]()
    topics.add("10_req_topic")
    consumer.subscribe(topics)

    try {
      while (true) {
        val records = consumer.poll(java.time.Duration.ofMillis(100))
        for (record <- records.asScala) {
          println(s"Received message: ${record.value()}")
          val processedMessage = process(record.value())
          Thread.sleep(500)
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
