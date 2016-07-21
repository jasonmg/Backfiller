package main.scala.taobao

import main.scala.core.{ConvertProvider, EntityCollection}
import main.scala.model.TaoBaoCSVEntity

/**
  * Created by Administrator on 2016-07-17.
  */
class TaoBaoConvertProvider extends ConvertProvider[TaoBaoCSVEntity] {

  def convert(source: TaoBaoCSVEntity): EntityCollection =
    EntityCollection(source)


}
