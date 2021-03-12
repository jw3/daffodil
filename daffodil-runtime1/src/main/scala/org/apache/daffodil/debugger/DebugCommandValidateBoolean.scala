package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateBoolean { self: DebugCommand =>
  override def validate(args: Seq[String]): Unit = {
    if (args.size != 1) {
      throw new DebugException("%s command requires a single argument".format(name))
    } else {
      val state = args.head
      if (state != "true" && state != "1" && state != "false" && state != "0") {
        throw new DebugException("argument must be true/false or 1/0")
      }
    }
  }
}
