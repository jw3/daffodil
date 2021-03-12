package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateSingleArg { self: DebugCommand =>
  override def validate(args: Seq[String]): Unit = {
    if (args.length != 1) {
      throw new DebugException("%s command requires a single argument".format(name))
    }
  }
}
