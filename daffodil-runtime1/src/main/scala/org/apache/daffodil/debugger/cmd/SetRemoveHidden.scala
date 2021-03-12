package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateBoolean
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetRemoveHidden extends DebugCommand with DebugCommandValidateBoolean {
    val name = "removeHidden"
    val desc = "set whether or not to remove Daffodil internal attributes when displaying the infoset (default: false)"
    val longDesc = """|Usage: set removeHidden|rh <value>
                      |
                      |Set whether or not hidden elements (e.g through the use of
                      |dfdl:hiddenGroupRef) should be displayed. This effects the 'eval' and
                      |'info infoset' commands. <value> must be either true/false or 1/0.
                      |Defaults to false.
                      |
                      |Example: set removeHidden true""".stripMargin
    override lazy val short = "rh"

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      val state = args.head
      DebuggerConfig.removeHidden =
        if (state == "true" || state == "1") {
          true
        } else {
          false
        }
      DebugState.Pause
    }
  }
