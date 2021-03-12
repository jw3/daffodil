package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger


object InfoBitLimit extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Long] {
        val name = "bitLimit"
        override lazy val short = "bl"
        val desc = "display the current bit limit"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[Long] = {
          if (state.bitLimit0b.isDefined) Some(state.bitLimit0b.get) else None
        }
      }

      object InfoBitPosition extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Long] {
        val name = "bitPosition"
        override lazy val short = "bp"
        val desc = "display the current bit position"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[Long] = {
          if (state.bitPos0b != -1) Some(state.bitPos0b) else None
        }
      }
