package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Complete extends DebugCommand with DebugCommandValidateZeroArgs {
      val name = "complete"
      val desc = "disable all debugger actions and continue"
      val longDesc = """|Usage: comp[lete]
                        |
                        |Continue parsing the input data until parsing is complete. All
                        |breakpoints are ignored.""".stripMargin
      override lazy val short = "comp"

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        DebuggerConfig.breakpoints.foreach(_.disable)
        DebuggerConfig.displays.foreach(_.disable)
        DebugState.Continue
      }
    }
