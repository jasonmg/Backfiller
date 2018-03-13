package com.jason.taobao.core

import com.jason.core.SourceProvider
import com.jason.taobao.model.TaoBaoEntity
import com.jason.taobao.model.Sex

class TaoBaoSourceProvider extends SourceProvider[TaoBaoEntity, TaoBaoSliceOut] {

  def load(sourceArg: TaoBaoSliceOut): Seq[TaoBaoEntity] = {
    sourceArg map { line =>
      val columns = line.trim.split(",")

      TaoBaoEntity(columns(0).toLong, columns(1), columns(2).toInt, Sex.withName(columns(3)),
        columns(4).toBoolean, columns(5), columns(6))
    }
  }
}
