package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object InfoPath extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "path"
        override lazy val short = "path"
        val desc = "display the current schema component designator/path"
        val longDesc = desc
        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          debugPrintln("%s: %s".format(name, processor.context.path))
          DebugState.Pause
        }
      }
