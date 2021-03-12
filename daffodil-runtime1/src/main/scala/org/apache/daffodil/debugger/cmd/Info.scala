package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.InfoBitLimit
import org.apache.daffodil.debugger.InfoBitPosition
import org.apache.daffodil.debugger.InfoBreakpoints
import org.apache.daffodil.debugger.InfoChildIndex
import org.apache.daffodil.debugger.InfoData
import org.apache.daffodil.debugger.InfoDelimiterStack
import org.apache.daffodil.debugger.InfoDiff
import org.apache.daffodil.debugger.InfoDisplays
import org.apache.daffodil.debugger.InfoFoundDelimiter
import org.apache.daffodil.debugger.InfoFoundField
import org.apache.daffodil.debugger.InfoGroupIndex
import org.apache.daffodil.debugger.InfoHidden
import org.apache.daffodil.debugger.InfoInfoset
import org.apache.daffodil.debugger.InfoOccursIndex
import org.apache.daffodil.debugger.InfoParser
import org.apache.daffodil.debugger.InfoPath
import org.apache.daffodil.debugger.InfoPointsOfUncertainty
import org.apache.daffodil.debugger.InfoUnparser
import org.apache.daffodil.debugger.InfoVariables
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Info extends DebugCommand {
  val name = "info"
  val desc = "display information"
  val longDesc = """|Usage: i[nfo] <item>...
                    |
                    |Print internal information to the console. <item> can be specified
                    |multiple times to display multiple pieces of information. <items>
                    |that are not recognized as info commands are assumed to be arguments
                    |to the previous <item>
                    |
                    |Example: info data infoset""".stripMargin
  override val subcommands =
    Seq(
      InfoBitLimit,
      InfoBitPosition,
      InfoBreakpoints,
      InfoChildIndex,
      InfoData,
      InfoDelimiterStack,
      InfoDiff,
      InfoDisplays,
      InfoFoundDelimiter,
      InfoFoundField,
      InfoGroupIndex,
      InfoHidden,
      InfoInfoset,
      InfoOccursIndex,
      InfoPath,
      InfoParser,
      InfoPointsOfUncertainty,
      InfoSuspensions,
      InfoUnparser,
      InfoVariables)

  /**
  * The info command allows printing multiple different kinds of
  * information at once. For example "info foo bar" is equivalent to
  * running the two commands "info foo" and "info bar". However, some info
  * commands might take one or more parameters. In this case, we allow
  * include the parameters after the subcommand, for example "info foo
  * fooParam bar". To determine where one subcommand end and another
  * subcommand begins, we assume unknown strings (e.g. fooParam) are parameters
  * to the previous known subcommand. This function constructs a sequence
  * that represents the different info commands and their parameters. For
  * example, the following command:
  *
  *   info foo bar barParam1 barParam2 baz
  *
  * Is parsed to the following:
  *
  *   Seq(
  *     Seq("foo"),
  *     Seq("bar", "barParam1", "barParam2"),
  *     Seq("baz"),
  *   )
  *
  * This sequence of sequences can then be used to determine how to execute
  * individual info commands and provide the appropriate arguments.
  */
  private def buildInfoCommands(args: Seq[String]): Seq[Seq[String]] = {
    val backwardsInfoCommands = args.foldLeft(Seq.empty[Seq[String]]) { case (infoCmds, arg) =>
      val cmd = subcommands.find(_ == arg)
      if (cmd.isDefined || infoCmds.isEmpty) {
        // Found a new info subcommand, or we don't have an info commands
        // yet. Create a new Seq to hold the subcommand + args and
        // prepend this Weq to our list of info subcommands. Note that if
        // this isn't actually an info subcommand, we'll detect that later
        // when we validate this list.
        val newCommand = Seq(arg)
        newCommand +: infoCmds
      } else {
        // Not a recognized info subcommand. Assume it is an arg to the
        // most recent command we've seen and prepend it to that list.
        val head :: tail = infoCmds
        (arg +: head) +: tail
      }
    }

    // We've built up a list of info commands with args, but the info
    // commands and the args are all reversed because we prepended
    // everything. So reverse that all to get the order correct
    backwardsInfoCommands.map { _.reverse }.reverse
  }

  override def validate(args: Seq[String]): Unit = {
    if (args.size == 0) {
      throw new DebugException("one or more commands are required")
    }
    val infocmds = buildInfoCommands(args)
    infocmds.foreach { cmds =>
      val cmd :: args = cmds
      subcommands.find(_ == cmd) match {
        case Some(c) => c.validate(args)
        case None => throw new DebugException("undefined info command: %s".format(cmd))
      }
    }
  }

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    val infocmds = buildInfoCommands(args)
    infocmds.foreach { cmds =>
      val cmd :: args = cmds
      val action = subcommands.find(_ == cmd).get
      action.act(args, state, processor)
    }
    DebugState.Pause
  }

  override def completer = new InfoCommandCompleter(this)
}
