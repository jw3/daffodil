package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger


object InfoHidden extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[Boolean] {
        val name = "hidden"
        override lazy val short = "h"
        val desc = "display whether or not we're within the nesting context of a hidden group"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Some[Boolean] = {
          Some(state.withinHiddenNest)
        }
      }
