package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Delete extends DebugCommand with DebugCommandValidateSubcommands {
      val name = "delete"
      val desc = "delete breakpoints and displays"
      val longDesc = """|Usage: d[elete] <type> <id>
                        |
                        |Remove a breakpoint or display.
                        |
                        |Example: delete breakpoint 1
                        |         delete display 1""".stripMargin
      override val subcommands = Seq(DeleteBreakpoint, DeleteDisplay)

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        val subcmd = args.head
        val subcmdArgs = args.tail
        subcommands.find(_ == subcmd).get.act(subcmdArgs, state, processor)
        DebugState.Pause
      }

      object DeleteBreakpoint extends DebugCommand with DebugCommandValidateInt {
        val name = "breakpoint"
        val desc = "delete a breakpoint"
        val longDesc = """|Usage: d[elete] b[reakpoint] <breakpoint_id>
                          |
                          |Remove a breakpoint created using the 'breakpoint' command.
                          |
                          |Example: delete breakpoint 1""".stripMargin

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          val id = args.head.toInt
          DebuggerConfig.breakpoints.find(_.id == id) match {
            case Some(b) => DebuggerConfig.breakpoints -= b
            case None => throw new DebugException("breakpoint %d not found".format(id))
          }
          DebugState.Pause
        }
      }

      object DeleteDisplay extends DebugCommand with DebugCommandValidateInt {
        val name = "display"
        val desc = "delete a display"
        val longDesc = """|Usage: d[elete] di[splay] <display_id>
                          |
                          |Remove a display created using the 'display' command.
                          |
                          |Example: delete display 1""".stripMargin
        override lazy val short = "di"

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          val id = args.head.toInt
          DebuggerConfig.displays.find(d => d.id == id) match {
            case Some(d) => DebuggerConfig.displays -= d
            case None => throw new DebugException("display %d not found".format(id))
          }
          DebugState.Pause
        }
      }
    }
