package main.scala.utils

import main.scala.core.BackfillerArgs
import org.kohsuke.args4j.{CmdLineException, CmdLineParser, OptionHandlerRegistry}

import scala.collection.JavaConverters._

trait CmdLineParserBase[Args <: BackfillerArgs] extends Log {
  def manifest: Manifest[Args]

  var cmdLine: Args = _
  var parser: CmdLineParser = _

  def main(args: Array[String]): Unit = {

    cmdLine = manifest.runtimeClass.newInstance().asInstanceOf[Args]
    parser = new CmdLineParser(cmdLine)
    try {
      parser.parseArgument(args.toList.asJava)
    } catch {
      case e: CmdLineException =>
        log.error(s"Error:${e.getMessage}\n Usage:\n")
        parser.printUsage(System.err)
        System.exit(1)
    }
  }
}


// TODO i aren't figure out how this part code fix implicit default args in class argument. copied from other place.
// should come back once have any explanation.
package magic {

  class DefaultTo[A, B]

  trait LowPriorityDefaultTo {
    implicit def overrideDefault[A, B] = new DefaultTo[A, B]
  }

  object DefaultTo extends LowPriorityDefaultTo {
    implicit def default[B] = new DefaultTo[B, B]
  }

}
