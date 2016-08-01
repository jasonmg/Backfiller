package main.scala.model

// this is the unify trait that every persistent class need inherit
trait Entity extends Serializable


trait EntityCollection {
  val entities: Seq[Entity]

  def size: Int = entities.size
}

object EntityCollection {

  def apply(ent: Entity): EntityCollection =
    new EntityCollection { val entities = Seq(ent) }

  def apply(ents: Seq[Entity]): EntityCollection =
    new EntityCollection{ val entities = ents }

  def reduce(ecs: Seq[EntityCollection]): EntityCollection ={
    val ents = ecs.reduce{(a,b)=> new EntityCollection{val entities = a.entities ++ b.entities}}
    ents
  }


}



