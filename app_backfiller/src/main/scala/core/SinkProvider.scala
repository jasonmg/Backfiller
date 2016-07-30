package main.scala.core

import java.io.{File, PrintWriter}
import main.scala.model.SinkMode._
import main.scala.utils.AutoClose._
import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._

trait SinkProvider {
  def insert(ele: EntityCollection): Unit
}

class DefaultSinkProvider(args: BackfillerArgs) extends SinkProvider {
  def insert(ele: EntityCollection): Unit = {
    store(args.sinkMode.get, ele)
  }

  val cache = new StringBuffer(100)

  def store(mode: SinkMode, ele: EntityCollection) = {
    val res = mode match {
      case JSON => toJSONOutput(ele.entities.map(_.asInstanceOf[EntityTpe]))
      case XML => toXMLOutput(ele.entities.map(_.asInstanceOf[EntityTpe]))
      case CSV => toCSVOutput(ele.entities.map(_.asInstanceOf[EntityTpe]))
      case _ => throw new IllegalArgumentException(s"unsupported sink mode: $mode")
    }
    cache.append(res)
  }

  def persistIntoFile(smokeFile: File): Unit = {
    using(new PrintWriter(smokeFile)) { printer =>
      printer.write(cache.toString)
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

//      sb.append("<root>")
      eleNValues map { eleNValue =>
        sb.append(s"<$runTimeClassName>")
        eleNValue map {
          case (tag, (tpe, value)) => sb.append(s"""<$tag tpe="$tpe">$value</$tag>""")
        }
        sb.append(s"</$runTimeClassName>")
      }
//      sb.append("</root>")

      sb.toString
    } else "<root></root>"

    res
  }

}

