package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger
import org.apache.daffodil.util.Misc


object InfoFoundDelimiter extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[String] {
        val name = "foundDelimiter"
        override lazy val short = "fd"
        val desc = "display the current found delimiter"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[String] = {
          if (state.delimitedParseResult.isDefined) {
            val dpr = state.delimitedParseResult.get
            val value = Misc.remapStringToVisibleGlyphs(dpr.matchedDelimiterValue.get)
            Some(value)
          } else {
            None
          }
        }
      }
