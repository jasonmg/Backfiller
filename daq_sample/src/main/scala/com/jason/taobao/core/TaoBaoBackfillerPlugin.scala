package com.jason.taobao.core

import com.jason.core.{BackfillerPlugin, BackfillerPluginCompanion}
import com.jason.taobao.model.TaoBaoEntity

object TaoBaoBackfillerPlugin extends BackfillerPluginCompanion[TaoBaoEntity, TaoBaoBackfillerArgs, TaoBaoSliceOut] {
  def apply(args: TaoBaoBackfillerArgs) = new TaoBaoBackfillerPlugin(args)
  def pluginIdentifier = "TaoBaoBackfillerPlugin"
}

class TaoBaoBackfillerPlugin(args: TaoBaoBackfillerArgs) extends BackfillerPlugin[TaoBaoEntity, TaoBaoBackfillerArgs, TaoBaoSliceOut](args) {
  def sliceProvider = new TaoBaoSliceProvider()

  def sourceProvider = new TaoBaoSourceProvider()

  def convertProvider = new TaoBaoConvertProvider()

  override def filterProvider = new TaoBaoFilterProvider()

  override def sinkProvider = new TaoBaoSinkProvider(args)
}
