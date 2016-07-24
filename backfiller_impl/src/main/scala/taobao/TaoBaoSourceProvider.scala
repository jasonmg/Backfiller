package main.scala.taobao

import main.scala.core.SourceProvider
import main.scala.model.{Sex, TaoBaoCSV}
import main.scala.model.TaoBaoCSVEntity

class TaoBaoSourceProvider extends SourceProvider[TaoBaoCSVEntity, TaoBaoSliceOut] {

  def load(sourceArg: TaoBaoSliceOut): Traversable[TaoBaoCSVEntity] = {

    sourceArg map { line =>
      val columns = line.trim.split(",")

      TaoBaoCSVEntity(columns(0).toLong, columns(1), columns(2).toInt, Sex.withName(columns(3)),
        columns(4).toBoolean, columns(5), columns(6))
    }
  }
}


object TaoBaoSourceProvider {

}
