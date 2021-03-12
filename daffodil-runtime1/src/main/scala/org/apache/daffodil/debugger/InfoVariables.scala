package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.processors.VariableInstance
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.StateForDebugger
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.VariableDefined
import org.apache.daffodil.processors.VariableRead
import org.apache.daffodil.processors.VariableSet
import org.apache.daffodil.processors.VariableInProcess
import org.apache.daffodil.processors.VariableUndefined

object InfoVariables extends DebugCommand with InfoDiffable {
  val name = "variables"
  override lazy val short = "v"
  val desc = "display in-scope state of variables"
  val longDesc = """|Usage: v[ariables] [<name>...]
                    |
                    |Display the in-scope state of variables matching <name>'s. If no
                    |names are given, displays the in-scope state of all variabes.""".stripMargin

  override def validate(args: Seq[String]): Unit = {
    // no validation
  }

  def variableInstanceToDebugString(vinst: VariableInstance): String = {
    val state = vinst.state match {
      case VariableDefined => "default"
      case VariableRead => "read"
      case VariableSet => "set"
      case VariableUndefined => "undefined"
      case VariableInProcess => "in process"
    }

    if (vinst.value.isEmpty) "(%s)".format(state)
    else "%s (%s)".format(vinst.value.value, state)
  }

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    val vmap = state.variableMap
    val allQNames = vmap.qnames
    val qnamesToPrint =
      if (args.size == 0) allQNames
      else {
        allQNames.filter { qname =>
          args.contains(qname.local) || args.contains(qname.toPrettyString)
        }
      }

    debugPrintln("%s:".format(name))
    qnamesToPrint.sortBy { _.toPrettyString }.foreach { qname =>
      val instance = vmap.find(qname).get
      val debugVal = variableInstanceToDebugString(instance)
      debugPrintln("  %s: %s".format(qname.toPrettyString, debugVal))
    }

    DebugState.Pause
  }

  def diff(pre: StateForDebugger, post: StateForDebugger): Boolean = {
    pre.variableMap.qnames.foldLeft(false) { case (foundDiff, qname) =>
      val pre_instance = pre.variableMap.find(qname).get
      val pre_value = pre_instance.value
      val pre_state = pre_instance.state

      val cur_instance = post.variableMap.find(qname).get
      val cur_value = cur_instance.value
      val cur_state = cur_instance.state

      if (pre_value != cur_value || pre_state != cur_state) {
        debugPrintln("variable: %s: %s -> %s".format(
          qname,
          variableInstanceToDebugString(pre_instance),
          variableInstanceToDebugString(cur_instance),
        ), "  ")
        foundDiff || true
      } else {
        foundDiff
      }
    }
  }
}
