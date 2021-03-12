package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger


object InfoGroupIndex extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Long] {
        val name = "groupIndex"
        override lazy val short = "gi"
        val desc = "display the current group index"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[Long] = {
          if (state.groupPos != -1) Some(state.groupPos) else None
        }
      }
