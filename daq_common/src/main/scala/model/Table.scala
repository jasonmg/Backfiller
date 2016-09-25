package main.scala.model

import main.scala.utils.Log
import scala.collection.mutable
import scala.collection.JavaConverters._

class Table(head: Row, var rows: mutable.Seq[Row] = mutable.Seq.empty) extends Log{

  import Table._

  def validate(columns: Seq[_]) = assert(columns.size == head.items.size, s"column number doesn't match to head, head: ${head.items.size}, row: ${columns.size} ")

  def addRow(row: Row): Unit = {
    validate(row.items)
    rows = rows :+ row
  }

  def addRow(row: Seq[String]): Unit = {
    validate(row)
    addRow(Row(row))
  }

  def print(f: String => Unit = println) = {
    val b = rows.+:(head)
    val columns = rows2Columns(b)

    val columnSize = columns.map(properWidth(_))
    log.debug(s"calcuate to proper width for each column: ${columnSize}")

    val h =  head.items zip columnSize
    val headStr = formatStr(h)
    val splitLine = List.fill(head.items.size)("=") zip columnSize
    val splitLineStr = formatStr(splitLine.toSeq,"=")

    val body = rows map { r => r.items zip columnSize }
    val bodyStrs = body.map(formatStr(_))

    f(headStr)
    f(splitLineStr)
    bodyStrs foreach f
  }

  def print1(f: String => Unit = println) = {
    val b = rows.+:(head)
    val SequenceOf1 = List.fill(head.size)(1)

    val h =  head.items zip SequenceOf1
    val headStr = formatStr1(h,"\t")

    val body = rows map { r => r.items zip SequenceOf1 }
    val bodyStrs = body.map(formatStr1(_,"\t"))

    f(headStr)
    bodyStrs foreach f
  }
}

object Table {
  def apply(head: Seq[String]) =
    new Table(Row(head))

  def rows2Columns(rows: Seq[Row]): Seq[Column] = {
    val head = rows.head
    (0 until head.items.size).map { idx =>
      rows map {
        _.items.apply(idx)
      }
    }.map(Column)
  }

  def properWidth(columns: Column, maxLimit: Int = 30) = {
    val maxLength = columns.items.foldLeft(0)((res, c) => Math.max(c.length, res))
    val properLength = Math.min(maxLength, maxLimit)
    properLength
  }

  def align(name: String, width: Int, withChar: String = " "): String =
    if (name.length > width) {
      name.substring(0, width - 3) + "..."
    } else {
      name + List.fill(width - name.length)(withChar).mkString
    }


  def formatStr(str: Seq[(String, Int)], withChar: String = " "): String ={
    str map {
      case (r, width) => align(r ,width, withChar)
    } mkString(" ")
  }

  def formatStr1(str: Seq[(String, Int)], withChar: String = " "): String ={
    str map {
      case (r, width) => r
    } mkString("\t")
  }
}

case class Row(items: Seq[String]){
  def size = items.size
}

case class Column(items: Seq[String])
