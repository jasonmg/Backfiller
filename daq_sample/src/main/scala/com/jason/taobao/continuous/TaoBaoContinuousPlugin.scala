package com.jason.taobao.continuous

import com.jason.core.{BackfillerPluginCompanion, ConvertProvider, DefaultSinkProvider, SourceProvider}
import com.jason.core.continuous.{BackfillerContinuousArgs, BackfillerContinuousPlugin, ContinuousSlice}
import com.jason.model.EntityCollection
import com.jason.taobao.model.TaoBaoEntity
import com.jason.utils.ElementReflectUtil

object TaoBaoContinuousPlugin extends BackfillerPluginCompanion[TaoBaoEntity, BackfillerContinuousArgs, ContinuousSlice] {
  def apply(args: BackfillerContinuousArgs) = new TaoBaoContinuousPlugin(args)
  def pluginIdentifier = "TaoBaoContinuousPlugin"
}

class TaoBaoContinuousPlugin(cmdLine: BackfillerContinuousArgs) extends BackfillerContinuousPlugin[TaoBaoEntity](cmdLine){
  def sourceProvider = new TaoBaoContinuousSourceProvider()
  def convertProvider = new TaoBaoContinuousConvertProvider()
  override def sinkProvider = new TaoBaoContinuousSinkProvider(cmdLine)
}

class TaoBaoContinuousConvertProvider extends ConvertProvider[TaoBaoEntity]{
  def convert(source: Seq[TaoBaoEntity]): EntityCollection =
    EntityCollection(source)
}

class TaoBaoContinuousSinkProvider(args: BackfillerContinuousArgs) extends DefaultSinkProvider(args){
  type EntityTpe = TaoBaoEntity
  override def toCSVOutput(entities: Seq[TaoBaoEntity]): Seq[String] = ElementReflectUtil.toCSV(entities)
}


