package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateBoolean
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetBreakOnFailure extends DebugCommand with DebugCommandValidateBoolean {
    val name = "breakOnFailure"
    val desc = "whether or not the debugger should break on failures (default: false)"
    val longDesc = """|Usage: set breakOnFailure|bof <value>
                      |
                      |Set whether or not the debugger should break on failures. If set to false
                      |the normal processing occurs. If set to true, any errors cause a break.
                      |Note that due to the backtracking behavior, not all failures are fatal.
                      |Defaults to false.
                      |
                      |Example: set breakOnFailure true""".stripMargin
    override lazy val short = "bof"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      val state = args.head
      DebuggerConfig.breakOnFailure =
        if (state == "true" || state == "1") {
          true
        } else {
          false
        }
      DebugState.Pause
    }
  }
