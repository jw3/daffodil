package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.StateForDebugger

trait InfoSimpleValue[A] extends InfoDiffable { self: DebugCommand =>

        def getSomeValue(state: StateForDebugger): Option[A]

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          val strValue = getSomeValue(state).map(_.toString).getOrElse("(no value)")
          debugPrintln("%s: %s".format(self.name, strValue))
          DebugState.Pause
        }

        def diff(pre: StateForDebugger, post: StateForDebugger): Boolean = {
          val valPre = getSomeValue(pre)
          val valPost = getSomeValue(post)
          if (valPre != valPost) {
            val strPre = valPre.map(_.toString).getOrElse("(no value)")
            val strPost = valPost.map(_.toString).getOrElse("(no value)")
            debugPrintln("%s: %s -> %s".format(self.name, strPre, strPost), "  ")
            true
          } else {
            false
          }
        }
      }
