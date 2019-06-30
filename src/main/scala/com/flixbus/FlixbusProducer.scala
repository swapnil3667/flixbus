package com.flixbus

import java.io.File

import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.flixbus.config.DriverConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.json4s.DefaultFormats
import org.json4s.jackson.Json

import scala.collection.JavaConverters._
import scala.io.Source


object FlixbusProducer {

  var inputTopic = "test_input"
  var kafkaProducerConfig: Map[String, AnyRef] = _

  case class flix(myKey: Int, myTimestamp: String)

  case class flixArray(payload: Array[flix])
  def main(args: Array[String]): Unit = {

    val config = ConfigParser.parseConfigFile[DriverConfig]("flixbus.yaml")
    kafkaProducerConfig = config.kafkaProducerConfig.asScala.toMap
    kafkaProducerConfig = kafkaProducerConfig + ("bootstrap.servers" -> System.getenv("HOST_IP"))
    inputTopic = System.getenv("INPUT_TOPIC")

    val kafkaProducer = new KafkaProducer[String, JsonNode](kafkaProducerConfig.asJava)
    //create object mapper
    val mapper = new ObjectMapper with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val json = Source.fromFile(args(0))
    val parsedJson = mapper.readValue[flixArray](json.reader())
    parsedJson.payload.foreach(x => {
      val jsonString = Json(DefaultFormats).write(x)
      val jsonNode: JsonNode = mapper.readTree(jsonString)
      println(jsonString)
      val producerRecord = new ProducerRecord[String, JsonNode](inputTopic, jsonNode)
      kafkaProducer.send(producerRecord)
    })
    kafkaProducer.flush()
    kafkaProducer.close()
  }

}
