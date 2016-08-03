package main.scala.taobao.core

import main.scala.core.ConvertProvider
import main.scala.model.EntityCollection
import main.scala.taobao.model.TaoBaoEntity


class TaoBaoConvertProvider extends ConvertProvider[TaoBaoEntity] {

  def convert(source: Traversable[TaoBaoEntity]): EntityCollection =
    EntityCollection(source.toSeq)
}
