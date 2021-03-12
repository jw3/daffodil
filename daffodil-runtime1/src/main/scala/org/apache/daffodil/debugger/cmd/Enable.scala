package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Enable extends DebugCommand with DebugCommandValidateSubcommands {
      val name = "enable"
      val desc = "enable breakpoints and displays"
      val longDesc = """|Usage: e[nable] <type> <id>
                        |
                        |Enable a breakpoint or display.
                        |
                        |Example: enable breakpoint 1
                        |         enable display 1""".stripMargin
      override val subcommands = Seq(EnableBreakpoint, EnableDisplay)

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        val subcmd = args.head
        val subcmdArgs = args.tail
        subcommands.find(_ == subcmd).get.act(subcmdArgs, state, processor)
        DebugState.Pause
      }

      

      
    }
