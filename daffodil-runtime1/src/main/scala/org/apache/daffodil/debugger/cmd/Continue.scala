package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Continue extends DebugCommand with DebugCommandValidateZeroArgs {
      val name = "continue"
      val desc = "continue parsing until a breakpoint is found"
      val longDesc = """|Usage: c[ontinue]
                        |
                        |Continue parsing the input data until a breakpoint is encountered. At
                        |which point, pause parsing and display a debugger console to the user.""".stripMargin

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebugState.Continue
      }
    }
