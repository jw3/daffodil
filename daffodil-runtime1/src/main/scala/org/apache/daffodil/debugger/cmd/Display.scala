package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Display extends DebugCommand with DebugCommandValidateSubcommands {
      val name = "display"
      val desc = "show value of expression each time program stops"
      val longDesc = """|Usage: di[splay] <debugger_command>
                        |
                        |Execute a debugger command (limited to eval, info, and clear) every time a
                        |there is a pause in the debugger.
                        |
                        |Example: display info infoset""".stripMargin
      override lazy val short = "di"
      override val subcommands = Seq(Eval, Info, Clear)

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebuggerConfig.displays += org.apache.daffodil.debugger.Display(DebuggerConfig.displayIndex, args)
        DebuggerConfig.displayIndex += 1
        DebugState.Pause
      }
    }
