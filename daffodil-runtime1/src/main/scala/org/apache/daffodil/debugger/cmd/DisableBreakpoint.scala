package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateInt
import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object DisableBreakpoint extends DebugCommand with DebugCommandValidateInt {
        val name = "breakpoint"
        val desc = "disable a breakpoint"
        val longDesc = """|Usage: dis[able] b[reakpoint] <breakpoint_id>
                          |
                          |Disable a breakpoint with the specified id. This causes the breakpoint
                          |to be skipped during debugging.
                          |
                          |Example: disable breakpoint 1""".stripMargin

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          val id = args.head.toInt
          DebuggerConfig.breakpoints.find(_.id == id) match {
            case Some(b) => b.disable
            case None => throw new DebugException("%d is not a valid breakpoint id".format(id))
          }
          DebugState.Pause
        }
      }
