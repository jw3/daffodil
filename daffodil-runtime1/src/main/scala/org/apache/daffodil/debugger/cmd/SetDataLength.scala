package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetDataLength extends DebugCommand with DebugCommandValidateInt {
    val name = "dataLength"
    val desc = "set the maximum number of bytes of the data to display. If negative, display all input data (default: 70)"
    val longDesc = """|Usage: set dataLength|dl <value>
                      |
                      |Set the number of bytes to display when displaying input data. If
                      |negative, display all input data. This only affects the 'info data'
                      |command. Defaults to 70 bytes.
                      |
                      |Example: set dataLength 100
                      |         set dataLength -1""".stripMargin
    override lazy val short = "dl"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.dataLength = args.head.toInt
      DebugState.Pause
    }
  }
