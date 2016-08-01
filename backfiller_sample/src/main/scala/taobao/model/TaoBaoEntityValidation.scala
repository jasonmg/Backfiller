package main.scala.taobao.model

object TaoBaoEntityValidation {

  def validate(head: String): Boolean = {
    val columns = head.trim.split(",")
    assert(columns.size == 7)

//    columns(0).trim == "id" &&
      columns(1).trim == "name" &&
      columns(2).trim == "age" &&
      columns(3).trim == "sex" &&
      columns(4).trim == "marriage" &&
      columns(5).trim == "address" &&
      columns(6).trim == "country"
  }

}

