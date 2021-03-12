package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object EnableDisplay extends DebugCommand with DebugCommandValidateInt {
        val name = "display"
        val desc = "enable a display"
        val longDesc = """|Usage: e[nable] di[splay] <display_id>
                          |
                          |Enable a display with the specified id. This causes the display command
                          |to be run during debugging.
                          |
                          |Example: enable display 1""".stripMargin

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          val id = args.head.toInt
          DebuggerConfig.displays.find(_.id == id) match {
            case Some(d) => d.enable
            case None => throw new DebugException("%d is not a valid display id".format(id))
          }
          DebugState.Pause
        }
      }
