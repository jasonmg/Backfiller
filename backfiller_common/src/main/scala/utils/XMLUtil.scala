package main.scala.utils

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._


object XMLUtil {

  /**
    * @param entity: the object you want retrieve
    * @return object parameter name and type
    */
  def getElementName[T: ru.TypeTag](entity: T): Seq[(String, Type)] = {
    val theType = ru.typeTag[T].tpe
    val res = for (
      f <- theType.decls if !f.isMethod
    ) yield (f.name.decodedName.toString.trim, f.typeSignature)
    res.toSeq
  }

  def getElementValue[T: ru.TypeTag : ClassTag](entity: T, eleName: Seq[String]): Map[String, Any] = {
    val rm = ru.runtimeMirror(entity.getClass.getClassLoader)
    val eleSymbols = eleName map { ele => (ele, ru.typeOf[T].decl(ru.TermName(ele)).asTerm) }

    val im = rm.reflect(entity)
    val res = eleSymbols map { case (ele, symbol) => (ele, im.reflectField(symbol).get) }

    res.toMap
  }

  def getRunTimeClassName[T: ru.TypeTag](entity: T): String =
    ru.typeTag[T].tpe.toString.split("\\.").last

  def getElementTpeValue[T: ru.TypeTag : ClassTag](entities: Seq[T], eleName: Map[String, Type]): Seq[Map[String, (Type, Any)]] =
    entities map { e =>
      getElementValue(e, eleName.keys.toSeq) map {
        case (ele, value) => (ele, (eleName(ele), value))
      }
    }

  def toXML[T: ru.TypeTag : ClassTag](entities: Seq[T]): String = {
    val res = if (entities.nonEmpty) {
      val eleNType = getElementName(entities.head)
      val eleNValues = getElementTpeValue(entities, eleNType.toMap)
      val runTimeClassName = getRunTimeClassName(entities.head)
      val sb = new StringBuffer(100)

      sb.append("<root>")
      eleNValues map { eleNValue =>
        sb.append(s"<$runTimeClassName>")
        eleNValue map {
          case (tag, (tpe, value)) => sb.append(s"""<$tag tpe="$tpe">$value</$tag>""")
        }
        sb.append(s"</$runTimeClassName>")
      }
      sb.append("</root>")

      sb.toString
    } else "<root></root>"

    res
  }

}
