package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.exceptions.Assert
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.parsers.PState
import org.apache.daffodil.processors.unparsers.UState


object InfoDelimiterStack extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "delimiterStack"
        val desc = "display the delimiter stack"
        val longDesc = desc
        override lazy val short = "ds"

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          debugPrintln("%s:".format(name))

          state match {
            case pstate: PState => {
              var i = 0
              while (i < pstate.mpstate.delimiters.length) {
                val typeString = if (i < pstate.mpstate.delimitersLocalIndexStack.top) "remote:" else "local: "
                val delim = pstate.mpstate.delimiters(i)
                debugPrintln("%s %s (%s)".format(typeString, delim.lookingFor, delim.delimType.toString.toLowerCase), "  ")
                i += 1
              }
            }
            case ustate: UState => {
              // TODO
            }
            case _ => Assert.impossibleCase()
          }

          DebugState.Pause
        }
      }
