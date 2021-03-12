package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Help extends DebugCommand {
      val name = "help"
      val desc = "display information about a command"
      val longDesc = """|Usage: h[elp] [command]
                        |
                        |Display help. If a command is given, display help information specific
                        |to that command and its subcommands.
                        |
                        |Example: help info""".stripMargin
      override val subcommands = Seq(Break, Clear, Complete, Condition, Continue, Delete, Disable, Display, Enable, Eval, History, Info, Quit, org.apache.daffodil.debugger.cmd.Set, Step, Trace)

      override def validate(args: Seq[String]): Unit = {
        // no validation
      }

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebugCommandBase.help(args)
        DebugState.Pause
      }
    }
