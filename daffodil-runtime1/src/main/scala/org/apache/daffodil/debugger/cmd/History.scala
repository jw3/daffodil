package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateOptionalArg
import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.debugger.debugPrintln
import org.apache.daffodil.exceptions.UnsuppressableException
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

import java.io.File

object History extends DebugCommand with DebugCommandValidateOptionalArg {
      val name = "history"
      override lazy val short = "hi"
      val desc = "display the history of commands"
      val longDesc = """|Usage: hi[story] [outfile]
                        |
                        |Display the history of commands. If an argument is given, write
                        |the history to the specified file rather then printing it to the
                        |screen.
                        |
                        |Example: history
                        |         history out.txt""".stripMargin

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        args.size match {
          case 0 => {
            debugPrintln("%s:".format(name))
            DebuggerConfig.history.zipWithIndex.foreach { case (cmd, index) => debugPrintln("%d: %s".format(index, cmd), "  ") }

          }
          case 1 => {
            try {
              val path =
                if (args.head.startsWith("~" + File.separator)) {
                  System.getProperty("user.home") + args.head.substring(1)
                } else {
                  args.head
                }
              val fw = new java.io.FileWriter(path)
              val bw = new java.io.BufferedWriter(fw)
              // use .init to drop the 'history outfile' command, we want
              // something that can be easily provided to the InteractiveDebugger constructor
              DebuggerConfig.history.init.foreach(cmd => {
                bw.write(cmd)
                bw.newLine()
              })
              bw.close()
              fw.close()
              debugPrintln("%s: written to %s".format(name, args.head))
            } catch {
              case s: scala.util.control.ControlThrowable => throw s
              case u: UnsuppressableException => throw u
              case e: Throwable => throw new DebugException("failed to write history file: " + e.getMessage())
            }
          }
        }
        DebugState.Pause
      }
    }
