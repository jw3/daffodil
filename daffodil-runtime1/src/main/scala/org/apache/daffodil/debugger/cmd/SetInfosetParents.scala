package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetInfosetParents extends DebugCommand with DebugCommandValidateInt {
    val name = "infosetParents"
    val desc = "set the number of parent elements to show when displaying the infoset (default: -1)"
    val longDesc = """|Usage: set infosetParents|ip <value>
                      |
                      |Set the number of parent elements to show when displaying the infoset.
                      |This only affects the 'info infoset' command. A value of zero will only
                      |show the current infoset element. A value of -1 will show the entire
                      |infoset. Defaults to -1.
                      |
                      |Example: set infosetParents 2""".stripMargin
    override lazy val short = "ip"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.infosetParents = args.head.toInt
      DebugState.Pause
    }
  }
