package com.flixbus

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.Collections

import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.flixbus.config.DriverConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConverters._


object FlixbusConsumer {

  var inputTopic = "test_input"
  var outputTopic = "test_output"
  var kafkaProducerConfig: Map[String, AnyRef] = _
  var kafkaConsumerConfig: Map[String, AnyRef] = _

  def main(args: Array[String]): Unit = {

    val config = ConfigParser.parseConfigFile[DriverConfig]("flixbus.yaml")
    kafkaConsumerConfig = config.kafkaConsumerConfig.asScala.toMap
    kafkaProducerConfig = config.kafkaProducerConfig.asScala.toMap
    kafkaConsumerConfig = kafkaConsumerConfig + ("bootstrap.servers" -> System.getenv("HOST_IP"))
    kafkaProducerConfig = kafkaProducerConfig + ("bootstrap.servers" -> System.getenv("HOST_IP"))
    inputTopic = System.getenv("INPUT_TOPIC")
    outputTopic = System.getenv("OUTPUT_TOPIC")

    val kafkaProducer = new KafkaProducer[String, JsonNode](kafkaProducerConfig.asJava)

    val kafkaConsumer = new KafkaConsumer[String, ObjectNode](kafkaConsumerConfig.asJava)
    kafkaConsumer.subscribe(Collections.singleton(inputTopic))

    val mapper = new ObjectMapper() with ScalaObjectMapper
    // needed for list, map and array
    mapper.registerModule(DefaultScalaModule)

    val root: ObjectNode = mapper.createObjectNode()

    System.out.println("Polling")
    val records = kafkaConsumer.poll(1000).asScala
    for(record <- records) {
      val timeStamp  = record.value().get("myTimestamp").toString.replace("\"","")
      if(timeStamp != "") {
        val toUTCTimestamp = OffsetDateTime.parse(timeStamp).withOffsetSameInstant(ZoneOffset.UTC).toString
        root.put("myKey", record.value().get("myKey").asInt())
        root.put("myTimestamp", toUTCTimestamp)
        println(record.offset() + "----" + root.toString)
        val producerRecord = new ProducerRecord[String, JsonNode](outputTopic, root)
        kafkaProducer.send(producerRecord)
      }
    }
    kafkaProducer.flush()
    kafkaProducer.close()
    kafkaConsumer.close()
  }

}
