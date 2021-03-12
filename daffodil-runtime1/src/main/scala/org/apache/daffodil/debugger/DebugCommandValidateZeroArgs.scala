package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateZeroArgs { self: DebugCommand =>
    override def validate(args: Seq[String]): Unit = {
      if (args.length != 0) {
        throw new DebugException("%s command requires zero arguments".format(name))
      }
    }
  }
