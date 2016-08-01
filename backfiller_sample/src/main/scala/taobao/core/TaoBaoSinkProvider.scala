package main.scala.taobao.core

import main.scala.core.DefaultSinkProvider
import main.scala.taobao.model.TaoBaoEntity
import main.scala.taobao.model.TaoBaoEntityJsonProtocol._
import main.scala.utils.XMLUtil
import spray.json._

class TaoBaoSinkProvider(args: TaoBaoBackfillerArgs) extends DefaultSinkProvider(args) {
  type EntityTpe = TaoBaoEntity

  override def toJSONOutput(entities: Seq[TaoBaoEntity]): String =
    entities.toJson.prettyPrint

  override def toXMLOutput(entities: Seq[TaoBaoEntity]):String = XMLUtil.toXML(entities)

}
