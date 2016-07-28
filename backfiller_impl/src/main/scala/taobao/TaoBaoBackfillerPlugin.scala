package main.scala.taobao

import main.scala.core.BackfillerPlugin
import main.scala.model.TaoBaoCSVEntity


class TaoBaoBackfillerPlugin(args: TaoBaoBackfillerArgs) extends BackfillerPlugin[TaoBaoCSVEntity, TaoBaoBackfillerArgs, TaoBaoSliceOut](args) {
  def sliceProvider = new TaoBaoSliceProvider()

  def sourceProvider = new TaoBaoSourceProvider()

  def convertProvider = new TaoBaoConvertProvider()

  override def sinkProvider = new TaoBaoSinkProvider(args)
}
