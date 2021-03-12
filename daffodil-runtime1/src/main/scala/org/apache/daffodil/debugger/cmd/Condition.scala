package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerConfig
import org.apache.daffodil.debugger.debugPrintln
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.util.DPathUtil

object Condition extends DebugCommand {
      val name = "condition"
      val desc = "set a DFDL expression to stop at breakpoint"
      val longDesc = """|Usage: cond[ition] <breakpoint_id> <dfdl_expression>
                        |
                        |Set a condition on a specified breakpoint. When a breakpoint
                        |is encountered, the debugger only pauses if the DFDL expression
                        |evaluates to true. If the result of the DFDL expression is not
                        |a boolean value, it is treated as false.
                        |
                        |Example: condition 1 dfdl:occursIndex() eq 3""".stripMargin
      override lazy val short = "cond"

      override def validate(args: Seq[String]): Unit = {
        if (args.length < 2) {
          throw new DebugException("condition command requires a breakpoint id and a DFDL expression")
        }

        val idArg = args.head
        val id = try {
          idArg.toInt
        } catch {
          case _: NumberFormatException => throw new DebugException("integer argument required")
        }

        DebuggerConfig.breakpoints.find(_.id == id).getOrElse {
          throw new DebugException("breakpoint %d not found".format(id))
        }
      }

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        val id = args.head.toInt
        val expression = args.tail.mkString(" ")
        val expressionWithBraces =
          if (!DPathUtil.isExpression(expression)) "{ " + expression + " }"
          else expression
        val b = DebuggerConfig.breakpoints.find(_.id == id).get
        b.condition = Some(expressionWithBraces)
        debugPrintln("%s: %s   %s".format(b.id, b.breakpoint, expressionWithBraces))
        DebugState.Pause
      }
    }
