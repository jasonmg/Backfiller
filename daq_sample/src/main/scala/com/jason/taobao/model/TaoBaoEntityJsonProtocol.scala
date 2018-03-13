package com.jason.taobao.model

import spray.json._

object TaoBaoEntityJsonProtocol extends DefaultJsonProtocol {

  implicit object TaoBaoCSVEntityJsonProtocol extends RootJsonFormat[TaoBaoEntity] {
    def write(e: TaoBaoEntity) = JsObject(
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
          TaoBaoEntity(id.toLong, name, age.toInt, Sex.withName(sex), marr, addr, country)
        case _ => throw new DeserializationException("TaoBaoEntity expected")
      }
    }
  }

}
