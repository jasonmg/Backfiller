package com.jason.core

import java.io.File

import com.jason.model.SinkMode
import com.jason.model.SinkMode._
import org.kohsuke.args4j
import org.kohsuke.args4j.{CmdLineParser, OptionDef, Option => Opt}
import org.kohsuke.args4j.spi.{OneArgumentOptionHandler, Setter}

class BackfillerArgs {

  @args4j.Option(name = "--plugin", usage = "provider your own FQ plugin name which contains your source|converter|sink logic",
    required = true)
  def setPlugin(name: String): Unit = {
    pluginName = name
  }

  var pluginName: String = null

  @args4j.Option(name = "--smokeFile", usage = "smokeFile is for save back fill date into it instead of in database or somewhere else",
    required = false)
  def setSmokeFile(name: String): Unit = {
    smokeFile = Some(new File(name))
  }

  var smokeFile: Option[File] = None

  @args4j.Option(name = "--failLogFile", usage = "this is for logging failed information inside actor system",
    required = false)
  def setFailLogFile(name: String): Unit = {
    failLogFile = Some(new File(name))
  }

  var failLogFile: Option[File] = None

  @args4j.Option(name = "--sinkMode", usage = "smokeFile is for save back fill date into it instead of in database or somewhere else",
    required = false)
  def setSinkMode(name: String): Unit = {
    sinkMode = SinkMode.withName(name)
  }

  var sinkMode: SinkMode = JSON
}

object BackfillerArgsHandler {

  def setup(): Unit = {
//        OptionHandlerRegistry.getRegistry.registerHandler(classOf[File], classOf[FileOptionOptionHandler])
  }

  class FileOptionOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter[Option[File]]) extends OneArgumentOptionHandler[Option[File]](parser, option, setter) {
    def parse(argument: String): Option[File] = if (argument.isEmpty) None else Some(new File(argument))
  }

  class EnumOptionOptionHandler[T <: Enumeration](parser: CmdLineParser, option: OptionDef, setter: Setter[Option[T#Value]], value: T) extends OneArgumentOptionHandler[Option[T#Value]](parser, option, setter) {
    def parse(argument: String): Option[T#Value] = if (argument.isEmpty) None else Some(value.withName(argument))
  }

  class SinkModeEnumOptionOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter[Option[SinkMode.SinkMode]]) extends EnumOptionOptionHandler(parser, option, setter, SinkMode)

}
