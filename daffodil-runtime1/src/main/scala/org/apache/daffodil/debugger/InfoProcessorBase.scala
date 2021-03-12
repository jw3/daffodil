package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

abstract class InfoProcessorBase extends DebugCommand with DebugCommandValidateZeroArgs {
  val desc = "display the current Daffodil " + name
  val longDesc = desc
  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    debugPrintln("%s: %s".format(name, processor.toBriefXML(2))) // only 2 levels of output, please!
    DebugState.Pause
  }
}
