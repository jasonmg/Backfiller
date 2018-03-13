package com.jason.taobao.core

import java.io.File
import java.nio.charset.CodingErrorAction

import com.jason.core.SliceProvider
import com.jason.taobao.model.TaoBaoEntityValidation._
import com.jason.utils.AutoClose._
import com.jason.utils.Log

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
  def sliceCSV(file: Option[File]): Seq[TaoBaoSliceOut] = {
    require(file.isDefined, "-csvFile is required")
    log.info(s"read csv file: ${file.get}")

    val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val res = using(Source.fromFile(file.get)(decoder)) { source =>
      val lines = source.getLines()

      val head = lines.next()
      assert(validate(head), s"invalid csv head: $head")

      val res = lines.grouped(sliceWidth).toList
      log.info(s"slicer group by: $sliceWidth, grouped size: ${res.size}")
      res
    }

    res.toSeq
  }
}

object TaoBaoSliceProvider {
  import com.jason.utils.ConfigUtil.config
  val sliceWidth = config.getConfig("backfiller-plugin").getInt("slicer-size")
}
