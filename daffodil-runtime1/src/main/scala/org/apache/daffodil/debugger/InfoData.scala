package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommand
import org.apache.daffodil.infoset.DIElement
import org.apache.daffodil.processors.DataLoc
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.processors.StateForDebugger
import org.apache.daffodil.schema.annotation.props.gen.Representation


object InfoData extends DebugCommand with DebugCommandValidateOptionalArg {
  val name = "data"
  val desc = "display the input/output data"
  val longDesc = desc

  def printData(rep: Option[Representation], l: Int, prestate: StateForDebugger, state: ParseOrUnparseState, processor: Processor): Unit = {
    val dataLoc = prestate.currentLocation.asInstanceOf[DataLoc]
    val lines = dataLoc.dump(rep, prestate.currentLocation, state)
    debugPrintln(lines, "  ")
  }

  override def validate(args: Seq[String]): Unit = {
    super.validate(args)
    args.headOption.map(_.toLowerCase) match {
      case None =>
      case Some("text") =>
      case Some("binary") =>
      case _ => throw new DebugException("unknown data representation: %s. Must be one of 'text' or 'binary'".format(args(0)))
    }
  }

  def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    debugPrintln("%s:".format(name))
    val rep = if (args.size > 0) {
      args(0).toLowerCase match {
        case "text" => Some(Representation.Text)
        case "binary" => Some(Representation.Binary)
      }
    } else {
      if (state.hasInfoset) {
        state.infoset match {
          case e: DIElement => Some(e.erd.impliedRepresentation)
        }
      } else {
        None
      }
    }

    val len = if (args.size > 1) {
      try {
        args(1).toInt
      } catch {
        case _: NumberFormatException => throw new DebugException("data length must be an integer")
      }
    } else {
      DebuggerConfig.dataLength
    }

    printData(rep, len, previousProcessorState, state, processor)
    DebugState.Pause
  }
}
