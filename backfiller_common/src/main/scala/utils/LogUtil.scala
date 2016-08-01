package main.scala.utils

import org.apache.log4j.Logger


trait Log {
  val log = Logger.getLogger(this.getClass)
}