package main.scala.core

import java.io.{File, PrintWriter}

import main.scala.model
import main.scala.model.Entity
import main.scala.model.SinkMode._
import main.scala.utils.AutoClose._
import main.scala.utils.XMLUtil

import scala.collection.mutable

trait SinkProvider {
  def insert(ele: model.EntityCollection): Unit
}

class DefaultSinkProvider(args: BackfillerArgs) extends SinkProvider {
  def insert(ele: model.EntityCollection): Unit = {
    store(ele)
  }

  val cache = new mutable.ListBuffer[EntityTpe]

  def store(ele: model.EntityCollection) = {
    cache ++= ele.entities.map(_.asInstanceOf[EntityTpe])
  }

  def persistIntoFile(smokeFile: File, mode: SinkMode): Unit = {
    def convert(): String = mode match {
      case JSON => toJSONOutput(cache)
      case XML => toXMLOutput(cache)
      case CSV => toCSVOutput(cache)
      case _ => throw new IllegalArgumentException(s"unsupported sink mode: $mode")
    }

    using(new PrintWriter(smokeFile)) { printer =>
      log.info(s"Write to file: $smokeFile")
      printer.write(convert())
    }
  }

  type EntityTpe <: Entity
  protected def toJSONOutput(entities: Seq[EntityTpe]): String = throw new RuntimeException("please implement toJSONOutput before invoke.")
  protected def toXMLOutput(entities: Seq[EntityTpe]): String = throw new RuntimeException("please use XMLUtil.toXML implement toXMLOutput before invoke.")
  protected def toCSVOutput(entities: Seq[EntityTpe]): String = throw new RuntimeException("please implement toCSVOutput before invoke.")
}

