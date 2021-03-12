package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object InfoDisplays extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "displays"
        override lazy val short = "di"
        val desc = "display the current 'display' expressions"
        val longDesc = desc
        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          if (DebuggerConfig.displays.size == 0) {
            debugPrintln("%s: no displays set".format(name))
          } else {
            debugPrintln("%s:".format(name))
            DebuggerConfig.displays.foreach { d =>
              {
                val enabledStr = if (d.enabled) "" else "*"
                debugPrintln("%s%s: %s".format(d.id, enabledStr, d.cmd.mkString(" ")), "  ")
              }
            }
          }
          DebugState.Pause
        }
      }
