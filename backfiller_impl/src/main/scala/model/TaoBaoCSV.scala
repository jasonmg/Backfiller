package main.scala.model

import scala.reflect.runtime._

import scala.reflect.runtime.{universe=> ru}
/**
  * Created by Administrator on 2016-07-17.
  */
object TaoBaoCSV {

  def validate(head: String): Boolean = {
    val columns = head.trim.split(",")
    assert(columns.size == 7)

    columns(0) == "id" &&
      columns(1) == "name" &&
      columns(2) == "age" &&
      columns(3) == "sex" &&
      columns(4) == "marriage" &&
      columns(5) == "address" &&
      columns(6) == "country"
  }

}




object Test extends App {

  class Foo {
    type T <:Any
    def test(a: T):String = ""
  }

  class FooBar extends Foo {
    type T =String
    override def test(a: String) = "aaa"
    def test1[T](b:T) = println(b)
  }

  private def getBaseClass[T: ru.TypeTag](entity:T):String ={
    ru.typeTag[T].tpe.typeSymbol.asClass.toString
  }
  println(getBaseClass(new FooBar))


  //  val f = ru.typeTag[Foo].tpe
  //  val b = ru.typeTag[FooBar].tpe
  //
  ////  assert(f =:= b,"a")
  //  assert(b <:< f,"b")
}
