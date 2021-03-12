package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger


object InfoOccursIndex extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Long] {
        val name = "occursIndex"
        override lazy val short = "oi"
        val desc = "display the current array limit"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[Long] = {
          if (state.arrayPos != -1) Some(state.arrayPos) else None
        }
      }
