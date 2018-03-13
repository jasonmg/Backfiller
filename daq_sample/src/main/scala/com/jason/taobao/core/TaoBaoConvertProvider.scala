package com.jason.taobao.core

import com.jason.core.ConvertProvider
import com.jason.model.EntityCollection
import com.jason.taobao.model.TaoBaoEntity


class TaoBaoConvertProvider extends ConvertProvider[TaoBaoEntity] {

  def convert(source: Seq[TaoBaoEntity]): EntityCollection ={
    EntityCollection(source)
  }
}
