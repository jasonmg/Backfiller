package main.scala.utils

import com.typesafe.config.ConfigFactory

object ConfigUtil extends App{

  val config = ConfigFactory.load()

  val con = config.getConfig("myapp1").withFallback(config)
  println(con)

}
