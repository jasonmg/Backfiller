package main.scala.core

import java.io.{File, PrintWriter}
import main.scala.model.SinkMode._
import main.scala.utils.AutoClose._
import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._
import scala.collection.mutable

trait SinkProvider {
  def insert(ele: EntityCollection): Unit
}

class DefaultSinkProvider(args: BackfillerArgs) extends SinkProvider {
  def insert(ele: EntityCollection): Unit = {
    store(ele)
  }

  val cache = new mutable.ListBuffer[EntityTpe]

  def store(ele: EntityCollection) = {
    cache ++= ele.entities.map(_.asInstanceOf[EntityTpe])
  }

  def persistIntoFile(smokeFile: File, mode: SinkMode): Unit = {
    def convert(): String = mode match {
      case JSON => toJSONOutput(cache)
      case XML => toXMLOutput(cache)
      case CSV => toCSVOutput(cache)
      case _ => throw new IllegalArgumentException(s"unsupported sink mode: $mode")
    }

    using(new PrintWriter(smokeFile)) { printer =>
      printer.write(convert())
    }
  }

  type EntityTpe <: Entity
  protected def toJSONOutput(entities: Seq[EntityTpe]): String = throw new RuntimeException("please implement toJSONOutput before invoke.")
  protected def toXMLOutput(entities: Seq[EntityTpe]): String =  throw new RuntimeException("please use toXMLOutputCommon implement toXMLOutput before invoke.")
  protected def toCSVOutput(entities: Seq[EntityTpe]): String = throw new RuntimeException("please implement toCSVOutput before invoke.")

  private[core] def getEntityElementName[T: ru.TypeTag](entity: T): Seq[(String, Type)] = {
    val theType = ru.typeTag[T].tpe
    val res = for (
      f <- theType.decls if !f.isMethod
    ) yield (f.name.decodedName.toString.trim, f.typeSignature)
    res.toSeq
  }

  private[core] def extractElementValue[T: ru.TypeTag : ClassTag](entity: T, eleName: Seq[String]): Map[String, Any] = {
    val rm = ru.runtimeMirror(entity.getClass.getClassLoader)
    val eleSymbols = eleName map { ele => (ele, ru.typeOf[T].decl(ru.TermName(ele)).asTerm) }

    val im = rm.reflect(entity)
    val res = eleSymbols map { case (ele, symbol) => (ele, im.reflectField(symbol).get) }

    res.toMap
  }

  private[core] def getRunTimeClassName[T: ru.TypeTag](entity: T): String =
    ru.typeTag[T].tpe.toString.split("\\.").last

  private def extractElementTpeValue[T: ru.TypeTag : ClassTag](entities: Seq[T], eleName: Map[String, Type]): Seq[Map[String, (Type, Any)]] =
    entities map { e =>
      extractElementValue(e, eleName.keys.toSeq) map {
        case (ele, value) => (ele, (eleName(ele), value))
      }
    }

  final def toXMLOutputCommon[T: ru.TypeTag : ClassTag](entities: Seq[T]): String = {
    val res = if (entities.nonEmpty) {
      val eleNType = getEntityElementName(entities.head)
      val eleNValues = extractElementTpeValue(entities, eleNType.toMap)
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

