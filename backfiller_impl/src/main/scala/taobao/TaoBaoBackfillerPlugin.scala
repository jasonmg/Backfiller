package main.scala.taobao

import main.scala.core.{BackfillerPlugin, BackfillerPluginCompanion}
import main.scala.model.TaoBaoCSVEntity

object TaoBaoBackfillerPlugin extends BackfillerPluginCompanion[TaoBaoCSVEntity, TaoBaoBackfillerArgs, TaoBaoSliceOut] {
  def apply(args: TaoBaoBackfillerArgs) = new TaoBaoBackfillerPlugin(args)
  def pluginIdentifier = "TaoBaoBackfillerPlugin"
}

class TaoBaoBackfillerPlugin(args: TaoBaoBackfillerArgs) extends BackfillerPlugin[TaoBaoCSVEntity, TaoBaoBackfillerArgs, TaoBaoSliceOut](args) {
  def sliceProvider = new TaoBaoSliceProvider()

  def sourceProvider = new TaoBaoSourceProvider()

  def convertProvider = new TaoBaoConvertProvider()

  override def sinkProvider = new TaoBaoSinkProvider(args)
}
