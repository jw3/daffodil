package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger


object InfoChildIndex extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Long] {
        val name = "childIndex"
        override lazy val short = "ci"
        val desc = "display the current child index"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[Long] = {
          if (state.childPos != -1) Some(state.childPos) else None
        }
      }
