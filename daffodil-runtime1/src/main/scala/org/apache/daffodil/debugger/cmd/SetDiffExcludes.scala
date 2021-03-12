package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor


object SetDiffExcludes extends DebugCommand {
    val name = "diffExcludes"
    val desc = "set info commands to exclude in the 'info diff' commanad"
    val longDesc = """|Usage: set diffExcludes|de <commands...>
                      |
                      |Set info comamnds to exclude in the 'info diff' command. Multiple arguments
                      |separated by a space excludes multiple commands. Zero arguments excludes no
                      |commands.
                      |
                      |Example: set diffExcludes bitPosition bitLimit""".stripMargin
    override lazy val short = "de"

    override def validate(args: Seq[String]): Unit = {
      val diffableNames = Info.InfoDiff.infoDiffables.map { _.name }
      val unknown = args.diff(diffableNames)
      if (unknown.size > 0) {
        throw new DebugException("unknown or undiffable info commands: " + unknown.mkString(", "))
      }
    }

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.diffExcludes = args
      DebugState.Pause
    }
  }
