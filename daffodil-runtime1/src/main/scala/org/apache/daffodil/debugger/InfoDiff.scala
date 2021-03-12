package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.debugger.cmd.Info
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object InfoDiff extends DebugCommand with DebugCommandValidateZeroArgs {
        val name = "diff"
        override lazy val short = "diff"
        val desc = "display differences since the previous pause in the debugger"
        val longDesc = desc

        lazy val infoDiffables = Info.subcommands.collect { case diffable: InfoDiffable => diffable }

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          debugPrintln("%s:".format(name))
          val foundDiff = infoDiffables.foldLeft(false) { case (prevCmdsFoundDiff, curCmd) =>
            val curCmdFoundDiff =
              if (DebuggerConfig.diffExcludes.contains(curCmd.name)) {
                false // skip current command since it's excluded
              } else {
                curCmd.diff(previousProcessorState, state)
              }
            curCmdFoundDiff || prevCmdsFoundDiff
          }
          if (!foundDiff) {
            debugPrintln("(no differences)", "  ")
          }

          DebugState.Pause
        }
      }
