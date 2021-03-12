package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object InfoBreakpoints extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "breakpoints"
        val desc = "display the current breakpoints"
        val longDesc = desc
        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          if (DebuggerConfig.breakpoints.size == 0) {
            debugPrintln("%s: no breakpoints set".format(name))
          } else {
            debugPrintln("%s:".format(name))
            DebuggerConfig.breakpoints.foreach { b =>
              {
                val enabledStr = if (b.enabled) "" else "*"
                debugPrintln("%s%s: %s   %s".format(b.id, enabledStr, b.breakpoint, b.condition.getOrElse("")), "  ")
              }
            }
          }
          DebugState.Pause
        }
      }
