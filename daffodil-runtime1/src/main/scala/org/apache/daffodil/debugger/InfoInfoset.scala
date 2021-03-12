package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.infoset.DIDocument
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object InfoInfoset extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "infoset"
        val desc = "display the current infoset"
        val longDesc = desc

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          debugPrintln("%s:".format(name))

          if (state.hasInfoset) {
            var parentSteps =
              if (DebuggerConfig.infosetParents < 0) Int.MaxValue
              else DebuggerConfig.infosetParents

            var node = state.infoset
            while (parentSteps > 0 && node.diParent != null) {
              node = node.diParent
              parentSteps -= 1
            }

            node match {
              case d: DIDocument if d.contents.size == 0 => {
                debugPrintln("No Infoset", "  ")
              }
              case _ => {
                val infosetString = infosetToString(node)
                val lines = infosetString.split("\n")

                val dropCount =
                  if (DebuggerConfig.infosetLines < 0) 0
                  else Math.max(0, lines.size - DebuggerConfig.infosetLines)
                if (dropCount > 0) {
                  debugPrintln("...", "  ")
                }
                val linesToShow = lines.drop(dropCount)
                linesToShow.foreach { l => debugPrintln(l, "  ") }
              }
            }
          } else {
            debugPrintln("No Infoset", "  ")
          }

          DebugState.Pause
        }
      }
