package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.Breakpoint
import org.apache.daffodil.debugger.DebugCommandValidateSingleArg
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.debugger.debugPrintln
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object Break extends DebugCommand with DebugCommandValidateSingleArg {
  val name = "break"
  val desc = "create a breakpoint"
  val longDesc = """|Usage: b[reak] <element_id>
                    |
                    |Create a breakpoint, causing the debugger to stop when the element
                    |with the <element_id> name is created.
                    |
                    |Example: break foo""".stripMargin

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    val bp = new Breakpoint(DebuggerConfig.breakpointIndex, args.head)
    DebuggerConfig.breakpoints += bp
    debugPrintln("%s: %s".format(bp.id, bp.breakpoint))
    DebuggerConfig.breakpointIndex += 1
    DebugState.Pause
  }
}
