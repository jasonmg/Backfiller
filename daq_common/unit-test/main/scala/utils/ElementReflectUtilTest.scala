package main.scala.utils

import org.scalatest.{FlatSpec, Matchers}
import scala.xml.XML
import scala.reflect.runtime.universe._


class ElementReflectUtilTest  extends FlatSpec with Matchers {
  private val u = ElementReflectUtil
  class Foo(val name: String, val age: Int)

  "ElementReflectUtil" should "able extract instance value" in {
    val foo = new Foo("test", 28)

    val res = u.getElementValue(foo, Seq("name", "age"))

    res should have size 2
    res should (contain key "name" and contain value "test")
    res should (contain key "age" and contain value 28)
  }

  it should "get runtime class name" in {
    val foo = new Foo("test", 28)
    val name = u.getRunTimeClassName(foo)

    name shouldEqual("Foo")
  }

  it should "able extract class parameter name and it's type" in {
    val foo = new Foo("test", 28)
    val res = u.getElementNameType(foo)

    res should have size 2
    res.head should equal ("name", typeOf[String])
    res.last should equal ("age", typeOf[Int])
  }

  it should "able generate xml print by given instances" in {
    val foo = new Foo("test", 28)
    val foo1 = new Foo("test1", 29)

    val res = u.toXML(Seq(foo,foo1))
    val resX = XML.loadString(res)
    val foos = resX \\ "Foo"

    foos.size should be (2)
    val f0Name = foos(0) \ "name"
    val nameTpe = f0Name \ "@tpe"
    nameTpe.text should === ("String")
    f0Name.text should === ("test")

    val f0Age = foos(0) \ "age"
    val ageTpe = f0Age \ "@tpe"
    ageTpe.text should === ("Int")
    f0Age.text should === ("28")
  }

}
