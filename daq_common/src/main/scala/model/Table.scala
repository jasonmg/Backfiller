package main.scala.model

class Table(head: Row, rows: Seq[Row] = Seq.empty){
  import Table._
  def validate(columns: Seq[_]) = assert(columns.size == head.row.size, s"column number doesn't match to head, head: ${head.row.size}, row: ${columns.size} ")

  def addRow(row: Row) = {
    validate(row.row)
    new Table(head, rows :+ row)
  }

  def addRow(row: Seq[String]) = {
    validate(row)
    addRow(convert(row))
  }

  def print() = {


  }


}
object Table{
  def apply(head: Seq[String]) =
    new Table(convert(head))

  def convert(row: Seq[String]): Row =
    Row(row map Column)
}

case class Row(row: Seq[Column])
case class Column(name: String)