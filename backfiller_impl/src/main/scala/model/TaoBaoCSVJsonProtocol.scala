package main.scala.model

import main.scala.model.TaoBaoCSVEntity
import spray.json._

/**
  * Created by Administrator on 2016-07-18.
  */
object TaoBaoCSVJsonProtocol extends DefaultJsonProtocol {

  implicit object TaoBaoCSVEntityJsonProtocol extends RootJsonFormat[TaoBaoCSVEntity] {
    def write(e: TaoBaoCSVEntity) = JsObject(
      "id" -> JsNumber(e.id),
      "name" -> JsString(e.name),
      "age" -> JsNumber(e.age),
      "sex" -> JsString(e.sex.toString),
      "marriage" -> JsBoolean(e.marriage),
      "address" -> JsString(e.address),
      "country" -> JsString(e.country)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "name", "age", "sex", "marrage", "address", "country") match {
        case Seq(JsNumber(id), JsString(name), JsNumber(age), JsString(sex), JsBoolean(marr), JsString(addr), JsString(country)) =>
          TaoBaoCSVEntity(id.toLong, name, age.toInt, Sex.withName(sex), marr, addr, country)
        case _ => throw new DeserializationException("TaoBaoCSVEntity expected")
      }
    }
  }

}
