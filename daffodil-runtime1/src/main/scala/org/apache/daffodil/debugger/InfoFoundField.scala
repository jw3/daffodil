package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.StateForDebugger
import org.apache.daffodil.util.Misc


object InfoFoundField extends DebugCommand with DebugCommandValidateZeroArgs with InfoSimpleValue[String] {
        val name = "foundField"
        override lazy val short = "ff"
        val desc = "display the current found field when delimiter scanning"
        val longDesc = desc

        def getSomeValue(state: StateForDebugger): Option[String] = {
          if (state.delimitedParseResult.isDefined) {
            val dpr = state.delimitedParseResult.get
            val value = Misc.remapStringToVisibleGlyphs(dpr.field.get)
            Some(value)
          } else {
            None
          }
        }
      }
