package org.apache.daffodil.debugger.cmd

import jline.console.completer.Completer
import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

import scala.collection.JavaConverters._

object DebugCommandBase extends DebugCommand with DebugCommandValidateSubcommands {
  val name = ""
  val desc = ""
  val longDesc = ""
  override lazy val short = ""
  override val subcommands = Seq(Break, Clear, Complete, Condition, Continue, Delete, Disable, Display, Enable, Eval,
    Help, History, Info, Quit, org.apache.daffodil.debugger.cmd.Set, Step, Trace)

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    val subcmd = args.head
    val subcmdArgs = args.tail
    val subcmdActor = subcommands.find(_ == subcmd).get
    val newState = subcmdActor.act(subcmdArgs, state, processor)
    newState
  }

  override def completer: Completer = {
    new DebugCommandCompleter(this) {
      override def complete(buffer: String, cursor: Int, candidates: java.util.List[CharSequence]): Int = {
        val cmd = buffer.replaceAll("^\\s+", "")
        val subcandidates = new java.util.ArrayList[CharSequence]
        val newCursor = subcommandsCompleter.complete(cmd, cursor, subcandidates)
        val seq = subcandidates.asScala
        seq.foreach(c => candidates.add(c))
        buffer.lastIndexOf(cmd) + newCursor
      }
    }
  }
}
