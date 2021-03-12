package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetInfosetLines extends DebugCommand with DebugCommandValidateInt {
    val name = "infosetLines"
    val desc = "set the maximum number of lines of the infoset to display (default: -1)"
    val longDesc = """|Usage: set infosetLines|il <value>
                      |
                      |Set the maximum number of lines to display when displaying the infoset.
                      |This only affects the 'info infoset' command. This shows the last
                      |<value> lines of the infoset. If <value> is less than or equal to zero,
                      |the entire infoset is printed. Defaults to -1.
                      |
                      |Example: set infosetLines 25""".stripMargin
    override lazy val short = "il"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.infosetLines = args.head.toInt
      DebugState.Pause
    }
  }
