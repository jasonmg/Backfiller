package main.scala.taobao.core

import java.io.File
import java.nio.charset.CodingErrorAction

import main.scala.core.SliceProvider
import main.scala.taobao.model.TaoBaoEntityValidation._
import main.scala.utils.AutoClose._
import main.scala.utils.Log

import scala.io.{Codec, Source}

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
    require(file.isDefined, "-csvFile is required")
    log.info(s"read csv file: ${file.get}")

    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val res = using(Source.fromFile(file.get)(decoder)) { source =>
      val lines = source.getLines()

      val head = lines.next()
      assert(validate(head), s"invalid csv head: $head")

      lines.grouped(sliceWidth).toList.toSeq
    }

    res
  }
}

object TaoBaoSliceProvider {
  val sliceWidth = 2
}
