package com.flixbus

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.io.Source
import scala.reflect.ClassTag

object ConfigParser {
  //todo to use Hadoop FileSystem API
  def parseConfigFile[T: ClassTag](configFile: String): T = {
    val input = Source.fromResource(configFile).reader()
    val yaml = new Yaml(new Constructor(implicitly[ClassTag[T]].runtimeClass))
    yaml.load(input).asInstanceOf[T]
  }
}
