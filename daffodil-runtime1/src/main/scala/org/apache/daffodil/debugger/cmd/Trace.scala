package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Trace extends DebugCommand with DebugCommandValidateZeroArgs {
      val name = "trace"
      val desc = "same as continue, but runs display commands during every step"
      val longDesc = """|Usage: t[race]
                        |
                        |Continue parsing the input data until a breakpoint is encountered,
                        |while running display commands after every parse step. When a
                        |breakpoint is encountered, pause parsing and display a debugger
                        |console to the user.""".stripMargin
      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebugState.Trace
      }
    }
