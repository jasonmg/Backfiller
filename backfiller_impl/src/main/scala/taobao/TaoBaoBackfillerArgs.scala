package main.scala.taobao

import main.scala.core.BackfillerArgs
import org.kohsuke.args4j.{Option => Ops}
import java.io.File
import main.scala.core.BackfillerArgs.FileOptionOptionHandler

/**
  * Created by Administrator on 2016-07-12.
  */
class TaoBaoBackfillerArgs extends BackfillerArgs {

  @Ops(name = "--csvFile", usage = "file which contain the record of taobao history",
    required = false, handler = classOf[FileOptionOptionHandler])
  val csvFile: Option[File] = None

}

