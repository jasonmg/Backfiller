package main.scala.core

import scala.xml.Elem

// this is the unify trait that every persistent class need inherit
sealed trait Entity extends Serializable

abstract class CSVEntity extends Entity
abstract class JsonEntity extends Entity
abstract class XMLEntity extends Entity


trait EntityCollection {
  def entities: Seq[Entity]

  def size: Int = entities.size
}

object EntityCollection {

  def apply(ent: Entity): EntityCollection =
    new EntityCollection {
      def entities = Seq(ent)
    }

}



