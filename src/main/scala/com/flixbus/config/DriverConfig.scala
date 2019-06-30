package com.flixbus.config

import java.{util => ju}

import scala.beans.BeanProperty


class DriverConfig {
  @BeanProperty var kafkaProducerConfig: ju.Map[String, AnyRef] = _
  @BeanProperty var kafkaConsumerConfig: ju.Map[String, AnyRef] = _
}
