package main.scala.taobao

import main.scala.core.DefaultSinkProvider
import main.scala.model.TaoBaoCSVEntity
import spray.json._
import main.scala.model.TaoBaoCSVJsonProtocol._

class TaoBaoSinkProvider(args: TaoBaoBackfillerArgs) extends DefaultSinkProvider(args) {
  type T = TaoBaoCSVEntity

  override def toJSONOutput(entities: Seq[TaoBaoCSVEntity]): String =
    entities.toJson.prettyPrint

}
