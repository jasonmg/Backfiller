package main.scala.taobao

import main.scala.core.BackfillerArgs
import org.kohsuke.args4j.{Option => Ops}
import java.io.File
import main.scala.core.BackfillerArgsHandler.FileOptionOptionHandler

class TaoBaoBackfillerArgs extends BackfillerArgs {

  @Ops(name = "--csvFile", usage = "file which contain the record of taobao history",
    required = false)
  def setCsvFile(file: String): Unit ={
    csvFile = Some(new File(file))
  }
  var csvFile: Option[File] = None

}

