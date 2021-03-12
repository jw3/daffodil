package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Disable extends DebugCommand with DebugCommandValidateSubcommands {
      val name = "disable"
      val desc = "disable breakpoints and displays"
      val longDesc = """|Usage: dis[able] <type> <id>
                        |
                        |Disable a breakpoint or display.
                        |
                        |Example: disable breakpoint 1
                        |         disable display 1""".stripMargin
      override lazy val short = "dis"
      override val subcommands = Seq(DisableBreakpoint, DisableDisplay)

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        val subcmd = args.head
        val subcmdArgs = args.tail
        subcommands.find(_ == subcmd).get.act(subcmdArgs, state, processor)
        DebugState.Pause
      }




    }
