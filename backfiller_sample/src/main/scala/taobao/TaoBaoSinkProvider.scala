package main.scala.taobao

import main.scala.core.DefaultSinkProvider
import main.scala.model.TaoBaoEntity
import spray.json._
import main.scala.model.TaoBaoEntityJsonProtocol._
import main.scala.utils.XMLUtil

class TaoBaoSinkProvider(args: TaoBaoBackfillerArgs) extends DefaultSinkProvider(args) {
  type EntityTpe = TaoBaoEntity

  override def toJSONOutput(entities: Seq[TaoBaoEntity]): String =
    entities.toJson.prettyPrint

  override def toXMLOutput(entities: Seq[TaoBaoEntity]):String = XMLUtil.toXML(entities)

}
