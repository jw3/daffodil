package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugCommandValidateSubcommands
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

object Set extends DebugCommand with DebugCommandValidateSubcommands {
  val name = "set"
  val desc = "modify debugger configuration"
  val longDesc = """|Usage: set <setting> <value>
                    |
                    |Change a debugger setting, the list of settings are below.
                    |
                    |Example: set breakOnlyOnCreation false
                    |         set dataLength 100""".stripMargin
  override val subcommands = Seq(
    SetBreakOnFailure,
    SetBreakOnlyOnCreation,
    SetDataLength,
    SetDiffExcludes,
    SetInfosetLines,
    SetInfosetParents,
    SetRemoveHidden,
    SetRepresentation,
    SetWrapLength,
  )
  override lazy val short = "set"

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    val subcmd = args.head
    val subcmdArgs = args.tail
    subcommands.find(_ == subcmd).get.act(subcmdArgs, state, processor)
    DebugState.Pause
  }


















}
