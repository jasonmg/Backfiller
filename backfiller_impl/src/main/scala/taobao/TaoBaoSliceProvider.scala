package main.scala.taobao

import java.io.File

import main.scala.core.SliceProvider
import main.scala.model.TaoBaoCSV
import main.scala.utils.AutoClose._
import main.scala.utils.Log
import TaoBaoCSV._

import scala.io.Source

/**
  * Created by Administrator on 2016-07-12.
  */
class TaoBaoSliceProvider extends SliceProvider[TaoBaoBackfillerArgs, TaoBaoSliceOut] with Log {
  import TaoBaoSliceProvider._

  def slice(args: TaoBaoBackfillerArgs): Seq[TaoBaoSliceOut] = {
    sliceCSV(args.csvFile)
  }

  /**
    * read whole csv content and slice by [[TaoBaoSliceProvider.sliceWidth]]
    * return seq of content
    */
  def sliceCSV(file: Option[File]): Seq[Seq[String]] = {
    require(file.isDefined, "-file is required")
    log.info(s"read csv file: ${file.get}")

    val res = using(Source.fromFile(file.get)) { source =>
      val lines = source.getLines()

      val head = lines.next()
      log.info(s"csv head is: $head")
      assert(validate(head))

      lines.grouped(sliceWidth).toSeq
    }

    res
  }
}

object TaoBaoSliceProvider {
  val sliceWidth = 2
}
