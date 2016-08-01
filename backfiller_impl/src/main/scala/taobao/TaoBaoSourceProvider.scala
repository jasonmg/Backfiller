package main.scala.taobao

import main.scala.core.SourceProvider
import main.scala.model._

class TaoBaoSourceProvider extends SourceProvider[TaoBaoEntity, TaoBaoSliceOut] {

  def load(sourceArg: TaoBaoSliceOut): Traversable[TaoBaoEntity] = {

    sourceArg map { line =>
      val columns = line.trim.split(",")

      TaoBaoEntity(columns(0).toLong, columns(1), columns(2).toInt, Sex.withName(columns(3)),
        columns(4).toBoolean, columns(5), columns(6))
    }
  }
}


object TaoBaoSourceProvider {

}
