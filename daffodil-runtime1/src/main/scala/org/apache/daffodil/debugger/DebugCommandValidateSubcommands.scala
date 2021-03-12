package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateSubcommands { self: DebugCommand =>
    override def validate(args: Seq[String]): Unit = {
      if (args.size == 0) {
        throw new DebugException("no command specified")
      }
      val subcmd = args.head
      val subcmdArgs = args.tail
      subcommands.find(_ == subcmd) match {
        case Some(c) => c.validate(subcmdArgs)
        case None => {
          throw new DebugException("undefined command: %s".format(subcmd))
        }
      }
    }
  }
