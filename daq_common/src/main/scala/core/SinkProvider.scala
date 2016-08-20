package main.scala.core

import java.io.{File, FileWriter}

import main.scala.model
import main.scala.model.Entity
import main.scala.model.SinkMode._
import main.scala.utils.AutoClose._
import main.scala.utils.TimeUtil._
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
    def convert(): Seq[String] = mode match {
      case JSON => toJSONOutput(cache)
      case XML => toXMLOutput(cache)
      case CSV => toCSVOutput(cache)
      case _ => throw new IllegalArgumentException(s"unsupported sink mode: $mode")
    }

    val result = convert()

    log.info(s"start write to file: ${smokeFile}")
    val (time, _) = timer {
      using(new FileWriter(smokeFile)) { printer =>
        result foreach printer.write
        printer.flush()
      }
    }
    log.info(s"writing to file is done, cost ${readableTime(time)}")
  }

  type EntityTpe <: Entity
  protected def toJSONOutput(entities: Seq[EntityTpe]): Seq[String] = throw new RuntimeException("please implement toJSONOutput before invoke.")
  protected def toXMLOutput(entities: Seq[EntityTpe]): Seq[String] = throw new RuntimeException("please use XMLUtil.toXML implement toXMLOutput before invoke.")
  protected def toCSVOutput(entities: Seq[EntityTpe]): Seq[String] = throw new RuntimeException("please implement toCSVOutput before invoke.")
}

