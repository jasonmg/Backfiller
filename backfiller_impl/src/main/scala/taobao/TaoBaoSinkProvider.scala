package main.scala.taobao

import main.scala.core.DefaultSinkProvider
import main.scala.model.TaoBaoCSVEntity
import spray.json._
import main.scala.model.TaoBaoCSVJsonProtocol._

class TaoBaoSinkProvider(args: TaoBaoBackfillerArgs) extends DefaultSinkProvider(args) {
  type EntityTpe = TaoBaoCSVEntity

  override def toJSONOutput(entities: Seq[TaoBaoCSVEntity]): String =
    entities.toJson.prettyPrint

  override def toXMLOutput(entities: Seq[TaoBaoCSVEntity]):String = toXMLOutputCommon(entities)

}
