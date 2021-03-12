package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object SetWrapLength extends DebugCommand with DebugCommandValidateInt {
    val name = "wrapLength"
    val desc = "set the maximum number of bytes to display before wrapping (default: 80)"
    val longDesc = """|Usage: set wrapLength|wl <value>
                      |
                      |Set the number of characters at which point output wraps. This only
                      |affects the 'info data' and 'info infoset' commands. A length less
                      |than or equal to zero disables wrapping. Defaults to 80 characters.
                      |
                      |Example: set wrapLength 100
                      |         set wrapLength -1""".stripMargin
    override lazy val short = "wl"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.wrapLength = args.head.toInt
      DebugState.Pause
    }
  }
