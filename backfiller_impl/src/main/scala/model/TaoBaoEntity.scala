package main.scala.model

case class TaoBaoEntity(id: Long,
                        name: String,
                        age: Int,
                        sex: Sex.Value,
                        marriage: Boolean,
                        address: String,
                        country: String) extends Entity