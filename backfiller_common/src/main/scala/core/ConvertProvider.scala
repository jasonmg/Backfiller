package main.scala.core

import main.scala.model.EntityCollection


trait ConvertProvider[In] {
  def convert(source: Seq[In]): EntityCollection
}
