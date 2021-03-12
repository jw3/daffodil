package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Quit extends DebugCommand with DebugCommandValidateZeroArgs {
  val name = "quit"
  val desc = "immediately abort all processing"
  val longDesc = """|Usage: q[uit]
                    |
                    |Immediately abort all processing.""".stripMargin
  override lazy val short = "q"
  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    sys.exit(1)
  }
}
