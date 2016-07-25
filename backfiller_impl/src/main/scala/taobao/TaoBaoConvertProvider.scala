package main.scala.taobao

import main.scala.core.{ConvertProvider, EntityCollection}
import main.scala.model.TaoBaoCSVEntity


class TaoBaoConvertProvider extends ConvertProvider[TaoBaoCSVEntity] {

  def convert(source: Seq[TaoBaoCSVEntity]): EntityCollection =
    EntityCollection(source)
}
