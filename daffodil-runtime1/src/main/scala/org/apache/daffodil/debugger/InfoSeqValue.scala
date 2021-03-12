package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.StateForDebugger

trait InfoSeqValue[A] extends InfoDiffable { self: DebugCommand =>

        def getSeqValue(state: StateForDebugger): Seq[A]

        def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
          debugPrintln("%s:".format(self.name))
          getSeqValue(state).foreach { value =>
            debugPrintln("%s".format(value.toString), "  ")
          }
          DebugState.Pause
        }

        def diff(pre: StateForDebugger, post: StateForDebugger): Boolean = {
          val preSeq = getSeqValue(pre)
          val postSeq = getSeqValue(post)
          val removed = preSeq.diff(postSeq)
          val added = postSeq.diff(preSeq)
          val hasDiff = removed.nonEmpty || added.nonEmpty
          if (hasDiff) {
            debugPrintln("%s:".format(self.name), "  ")
            removed.foreach { v =>
              debugPrintln("- %s".format(v.toString), "    ")
            }
            added.foreach { v =>
              debugPrintln("+ %s".format(v.toString), "    ")
            }
          }
          hasDiff
        }
      }
