package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Step extends DebugCommand with DebugCommandValidateZeroArgs {
      val name = "step"
      val desc = "execute a single parser step"
      val longDesc = """|Usage: s[tep]
                        |
                        |Perform a single parse action, pause parsing, and display a debugger
                        |prompt.""".stripMargin
      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebugState.Step
      }
    }
