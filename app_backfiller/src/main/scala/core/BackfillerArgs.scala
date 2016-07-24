package main.scala.core

import java.io.File

import main.scala.model.SinkMode
import main.scala.model.SinkMode._
import org.kohsuke.args4j.{CmdLineParser, OptionDef, OptionHandlerRegistry, Option => Ops}
import org.kohsuke.args4j.spi.{OneArgumentOptionHandler, Setter}

trait BackfillerArgs {

  import BackfillerArgs._

  @Ops(name = "--plugin", usage = "provider your own FQ plugin name which contains your source|converter|sink logic",
    required = true)
  val pluginName = null

  @Ops(name = "--smokeFile", usage = "smokeFile is for save back fill date into it instead of in database or somewhere else",
    handler = classOf[FileOptionOptionHandler], required = false)
  val smokeFile: Option[File] = None

  @Ops(name = "--sinkMode", usage = "smokeFile is for save back fill date into it instead of in database or somewhere else",
    handler = classOf[SinkModeEnumOptionOptionHandler], required = false)
  val sinkMode: Option[SinkMode] = None
}

object BackfillerArgs {

  def setup(): Unit = {
    OptionHandlerRegistry.getRegistry.registerHandler(classOf[File], classOf[FileOptionOptionHandler])
  }

  class FileOptionOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter[Option[File]]) extends OneArgumentOptionHandler[Option[File]](parser, option, setter) {
    def parse(argument: String): Option[File] = if (argument.isEmpty) None else Some(new File(argument))
  }

  class EnumOptionOptionHandler[T <: Enumeration](parser: CmdLineParser, option: OptionDef, setter: Setter[Option[T#Value]], value: T) extends OneArgumentOptionHandler[Option[T#Value]](parser, option, setter) {
    def parse(argument: String): Option[T#Value] = if (argument.isEmpty) None else Some(value.withName(argument))
  }

  class SinkModeEnumOptionOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter[Option[SinkMode.SinkMode]]) extends EnumOptionOptionHandler(parser, option, setter, SinkMode)

}
