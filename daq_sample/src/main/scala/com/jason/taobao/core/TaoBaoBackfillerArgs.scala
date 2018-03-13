package com.jason.taobao.core

import java.io.File

import com.jason.core.BackfillerArgs
import org.kohsuke.args4j.{Option => Ops}

class TaoBaoBackfillerArgs extends BackfillerArgs {

  @Ops(name = "--csvFile", usage = "file which contain the record of com.jason.taobao history",
    required = false)
  def setCsvFile(file: String): Unit ={
    csvFile = Some(new File(file))
  }
  var csvFile: Option[File] = None

}

