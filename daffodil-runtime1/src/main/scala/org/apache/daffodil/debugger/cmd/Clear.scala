package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateZeroArgs
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object Clear extends DebugCommand with DebugCommandValidateZeroArgs {
      val name = "clear"
      val desc = "clear the screen"
      val longDesc = """|Usage: cl[ear]
                        |
                        |Clear the screen.""".stripMargin
      override lazy val short = "cl"

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        print(27: Char)
        print('[')
        print("2J")
        print(27: Char)
        print('[')
        print("1;1H")

        DebugState.Pause
      }
    }
