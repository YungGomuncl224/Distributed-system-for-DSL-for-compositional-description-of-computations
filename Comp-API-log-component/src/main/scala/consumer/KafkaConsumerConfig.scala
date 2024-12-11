package consumer


import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}

import java.util
import java.util.Properties
import java.util.ArrayList
import scala.collection.JavaConverters.*

val n = 5.toBinaryString
class KafkaConsumerConfig {
  
  private val consumer : KafkaConsumer[String,String] = createConsumer()
  
  private def createConsumer(): KafkaConsumer[String, String] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    new KafkaConsumer[String, String](props)
  }
  
  def subscribe(topics : util.ArrayList[String]) : Unit = {
    consumer.subscribe(topics)
  }

  def poll(timeout: java.time.Duration): ConsumerRecords[String, String] = consumer.poll(timeout)
  
  def close() : Unit = consumer.close()
  
}