package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand

trait DebugCommandValidateInt { self: DebugCommand =>
  override def validate(args: Seq[String]): Unit = {
    if (args.size != 1) {
      throw new DebugException("%s command requires a single argument".format(name))
    } else {
      try {
        args.head.toInt
      } catch {
        case _: NumberFormatException => throw new DebugException("integer argument is required")
      }
    }
  }
}
