package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateBoolean
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetBreakOnlyOnCreation extends DebugCommand with DebugCommandValidateBoolean {
    val name = "breakOnlyOnCreation"
    val desc = "whether or not breakpoints should occur only on element creation, or always (default: true)"
    val longDesc = """|Usage: set breakOnlyOnCreation|booc <value>
                      |
                      |Set whether or not breakpoints should only be evaluated on element creation.
                      |<value> must be either true/false or 1/0. If true, breakpoints only stop on
                      |element creation. If false, breakpoints stop anytime a parser interacts with
                      |an element. Defaults to true.
                      |
                      |Example: set breakOnlyOnCreation false""".stripMargin
    override lazy val short = "booc"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      val state = args.head
      DebuggerConfig.breakOnlyOnCreation =
        if (state == "true" || state == "1") {
          true
        } else {
          false
        }
      DebugState.Pause
    }
  }
