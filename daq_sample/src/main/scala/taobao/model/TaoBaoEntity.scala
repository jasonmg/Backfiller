package main.scala.taobao.model

import main.scala.model.Entity

case class TaoBaoEntity(id: Long,
                        name: String,
                        age: Int,
                        sex: Sex.Value,
                        marriage: Boolean,
                        address: String,
                        country: String) extends Entity