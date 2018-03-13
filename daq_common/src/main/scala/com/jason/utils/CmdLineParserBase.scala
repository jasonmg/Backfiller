package com.jason.utils

import com.jason.core.BackfillerArgs
import org.kohsuke.args4j.{CmdLineException, CmdLineParser}

import scala.collection.JavaConverters._


trait CmdLineParserBase[Args <: BackfillerArgs] extends Log {
  def manifest: Manifest[Args] // carry class type info from compile time to run time

  var cmdLine: Args = _
  var parser: CmdLineParser = _

  def main(args: Array[String]): Unit = {

    cmdLine = manifest.runtimeClass.newInstance().asInstanceOf[Args]
    parser = new CmdLineParser(cmdLine)
    try {
      parser.parseArgument(args.toArray: _*)
    } catch {
      case e: CmdLineException =>
        log.error(s"Error:${e.getMessage}\n Usage:\n")
        parser.printUsage(System.err)
        System.exit(1)
    }
  }
}

package magic{
  class DefaultTo[A, B]

  trait LowPriorityDefaultTo {
    implicit def overrideDefault[A, B] = new DefaultTo[A, B]
  }

  object DefaultTo extends LowPriorityDefaultTo {
    implicit def default[B] = new DefaultTo[B, B]
  }
}
