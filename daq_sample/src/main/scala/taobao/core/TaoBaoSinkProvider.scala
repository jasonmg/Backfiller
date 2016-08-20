package main.scala.taobao.core

import main.scala.core.DefaultSinkProvider
import main.scala.taobao.model.TaoBaoEntity
import main.scala.taobao.model.TaoBaoEntityJsonProtocol._
import main.scala.utils.ElementReflectUtil
import spray.json._

class TaoBaoSinkProvider(args: TaoBaoBackfillerArgs) extends DefaultSinkProvider(args) {
  type EntityTpe = TaoBaoEntity

  override def toJSONOutput(entities: Seq[TaoBaoEntity]): Seq[String] =
    entities map {_.toJson.prettyPrint}

  override def toXMLOutput(entities: Seq[TaoBaoEntity]): Seq[String] = ElementReflectUtil.toXML(entities)

  override def toCSVOutput(entities: Seq[TaoBaoEntity]): Seq[String] = ElementReflectUtil.toCSV(entities)

}
