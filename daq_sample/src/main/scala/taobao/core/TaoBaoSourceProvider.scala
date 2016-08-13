package main.scala.taobao.core

import main.scala.core.SourceProvider
import main.scala.taobao.model.TaoBaoEntity
import main.scala.taobao.model.Sex

class TaoBaoSourceProvider extends SourceProvider[TaoBaoEntity, TaoBaoSliceOut] {

  def load(sourceArg: TaoBaoSliceOut): Seq[TaoBaoEntity] = {

    sourceArg map { line =>
      val columns = line.trim.split(",")

      TaoBaoEntity(columns(0).toLong, columns(1), columns(2).toInt, Sex.withName(columns(3)),
        columns(4).toBoolean, columns(5), columns(6))
    }
  }
}