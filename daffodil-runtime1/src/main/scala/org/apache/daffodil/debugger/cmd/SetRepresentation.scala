package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.schema.annotation.props.gen.Representation


object SetRepresentation extends DebugCommand {
    val name = "representation"
    val desc = "set the output when displaying data (default: text)"
    val longDesc = """|Usage: set representation|rp <value>
                      |
                      |Set the output when displaying data. <value> must be either
                      |'text' or 'binary'. Defaults to 'text'.
                      |Defaults to false.
                      |
                      |Example: set representation binary""".stripMargin
    override lazy val short = "rp"

    override def validate(args: Seq[String]): Unit = {
      if (args.size != 1) {
        throw new DebugException("a single argument is required")
      } else {
        args.head.toLowerCase match {
          case "text" =>
          case "binary" =>
          case _ => throw new DebugException("argument must be either 'text' or 'binary'")
        }
      }
    }

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      DebuggerConfig.representation = args.head.toLowerCase match {
        case "text" => Representation.Text
        case "binary" => Representation.Binary
      }
      DebugState.Pause
    }
  }
