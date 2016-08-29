package main.scala.utils

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._

/** use scala reflection to extract object's filed name, type and values */
object ElementReflectUtil {

  def getElementSymbol[T: ru.TypeTag](entity: T): Seq[Symbol] = {
    val theType = ru.typeTag[T].tpe
    val res = for (
      f <- theType.decls if !f.isMethod
    ) yield f
    res.toSeq
  }

  def getElementName[T: ru.TypeTag](entity: T): Seq[String] =
    getElementSymbol(entity) map { _.name.decodedName.toString.trim }

  def getElementNameType[T: ru.TypeTag](entity: T): Map[String, Type] =
    getElementSymbol(entity) map {s => (s.name.decodedName.toString.trim, s.typeSignature) } toMap

  def getElementValue[T: ru.TypeTag : ClassTag](entity: T, eleName: Seq[String]): Seq[Any] = {
    val rm = ru.runtimeMirror(entity.getClass.getClassLoader)
    val eleTermSymbols = eleName map { ele => ru.typeOf[T].decl(ru.TermName(ele)).asTerm }

    val im = rm.reflect(entity)
    val res = eleTermSymbols map { symbol => im.reflectField(symbol).get }

    res
  }

  def getElementNameValue[T: ru.TypeTag : ClassTag](entity: T, eleName: Seq[String]): Map[String, Any] = {
    val res = eleName zip getElementValue(entity, eleName)
    res.toMap
  }

  def getRunTimeClassName[T: ru.TypeTag](entity: T): String =
    ru.typeTag[T].tpe.toString.split("\\.").last

  def getElementTpeValue[T: ru.TypeTag : ClassTag](entities: Seq[T]): Seq[Map[String, (Type, Any)]] ={
    val entity = entities.head
    val entityNameType = getElementNameType(entity)
    entities map { e =>
      getElementNameValue(e, entityNameType.keySet.toSeq) map {
        case (ele, value) => (ele, (entityNameType(ele), value))
      }
    }
  }

  def toXML[T: ru.TypeTag : ClassTag](entities: Seq[T]): Seq[String] = {
    val res = if (entities.nonEmpty) {
      val eleNValues = getElementTpeValue(entities)
      val runTimeClassName = getRunTimeClassName(entities.head)

      eleNValues map { eleNValue =>
        val sb = new StringBuffer(100)
        sb.append(s"<$runTimeClassName>")
        eleNValue map {
          case (tag, (tpe, value)) => sb.append(s"""<$tag tpe="$tpe">$value</$tag>""")
        }
        sb.append(s"</$runTimeClassName>")
        sb.toString
      }
    } else Seq("null")

    "<root>" +: res :+ "</root>"
  }

  def toCSV[T: ru.TypeTag : ClassTag](entities: Seq[T]): Seq[String] = {
    val res = if (entities.nonEmpty) {
      val eleName = getElementName(entities.head)
      val headStr = eleName.mkString(",")
      val values = entities map { entity =>
        val eleValue = getElementValue(entity, eleName)
        eleValue.mkString(",")
      }

      values.+:(headStr)
    } else Seq("Null")

     res
  }

}
