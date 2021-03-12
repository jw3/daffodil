package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateOptionalArg { self: DebugCommand =>
  override def validate(args: Seq[String]): Unit = {
    if (args.size > 1) {
      throw new DebugException("%s command zero or one arguments".format(name))
    }
  }
}
