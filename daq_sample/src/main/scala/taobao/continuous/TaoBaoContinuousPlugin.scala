package main.scala.taobao.continuous

import main.scala.core.{ConvertProvider, SourceProvider}
import main.scala.core.continuous.{BackfillerContinuousArgs, BackfillerContinuousPlugin, ContinuousSlice}
import main.scala.model.EntityCollection
import main.scala.taobao.model.TaoBaoEntity

class TaoBaoContinuousPlugin(cmdLine: BackfillerContinuousArgs) extends BackfillerContinuousPlugin[TaoBaoEntity](cmdLine){
  def sourceProvider = new TaoBaoContinuousSourceProvider()
  def convertProvider = new TaoBaoContinuousConvertProvider()
}

class TaoBaoContinuousConvertProvider extends ConvertProvider[TaoBaoEntity]{
  def convert(source: Seq[TaoBaoEntity]): EntityCollection =
    EntityCollection(source)
}


