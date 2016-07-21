package main.scala.model

import main.scala.core.CSVEntity

import scala.xml.Elem

/**
  * Created by Administrator on 2016-07-18.
  */
case class TaoBaoCSVEntity(id: Long,
                           name: String,
                           age: Int,
                           sex: Sex.Value,
                           marriage: Boolean,
                           address: String,
                           country: String) extends CSVEntity