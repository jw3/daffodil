package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.parsers.PState


object InfoPointsOfUncertainty extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "pointsOfUncertainty"
        override lazy val short = "pou"
        val desc = "display list of unresolved points of uncertainty"
        val longDesc = desc
        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          state match {
            case state: PState => {
              debugPrintln("%s:".format(name))
              val pous = state.pointsOfUncertainty.toList
              if (pous.isEmpty) {
                debugPrintln("  (none)")
              } else {
                pous.reverse.foreach { pou =>
                  debugPrintln("  %s".format(pou.toString))
                }
              }
            }
            case _ => debugPrintln("%s: info only available for parse steps".format(name))
          }
          DebugState.Pause
        }
      }
