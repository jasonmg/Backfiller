package com.jason.core

import java.io.{File, FileWriter}

import com.jason.model.{Entity, EntityCollection}
import com.jason.model.SinkMode._
import com.jason.utils.AutoClose._
import com.jason.utils.TimeUtil._

import scala.collection.mutable

trait SinkProvider {
  def insert(ele: EntityCollection): Unit
}

class DefaultSinkProvider(args: BackfillerArgs) extends SinkProvider {
  def insert(ele: EntityCollection): Unit = {
    store(ele)
  }

  val cache = new mutable.ListBuffer[EntityTpe]

  def store(ele: EntityCollection) = {
    cache ++= ele.entities.map(_.asInstanceOf[EntityTpe])
  }

  def persistIntoFile(smokeFile: File, mode: SinkMode): Unit = {
    def convert(): Seq[String] = mode match {
      case JSON => "[" +: toJSONOutput(cache) :+ "]"
      case XML => toXMLOutput(cache)
      case CSV => toCSVOutput(cache)
      case _ => throw new IllegalArgumentException(s"unsupported sink mode: $mode")
    }

    val result = convert()

    log.info(s"start write to file: ${smokeFile}")
    val (time, _) = timer {
      using(new FileWriter(smokeFile)) { printer =>
        result foreach { r =>
          printer.write(r)
          printer.write("\n")
        }
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

