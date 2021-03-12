package org.apache.daffodil.debugger.cmd

import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.DebuggerHost
import org.apache.daffodil.debugger.debugPrettyPrintXML
import org.apache.daffodil.debugger.debugPrintln
import org.apache.daffodil.debugger.debuggerQName
import org.apache.daffodil.dpath.NodeInfo
import org.apache.daffodil.dsom.RelativePathPastRootError
import org.apache.daffodil.dsom.RuntimeSchemaDefinitionError
import org.apache.daffodil.infoset.InfosetElement
import org.apache.daffodil.infoset.InfosetNoDataExceptionBase
import org.apache.daffodil.oolag.ErrorsNotYetRecorded
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor
import org.apache.daffodil.util.DPathUtil
import org.apache.daffodil.util.Misc

object Eval extends DebugCommand {
      val name = "eval"
      val desc = "evaluate a DFDL expression"
      override lazy val short = "ev"
      val longDesc = """|Usage: ev[al] <dfdl_expression>
                        |
                        |Evaluate a DFDL expression.
                        |
                        |Example: eval dfdl:occursIndex()""".stripMargin

      override def validate(args: Seq[String]): Unit = {
        if (args.size == 0) {
          throw new DebugException("eval requires a DFDL expression")
        }
      }

      def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
        val expressionList = args
        val expression = expressionList.mkString(" ")

        if (!state.hasInfoset) {
          debugPrintln("eval: There is no infoset currently.")
          return DebugState.Pause
        }
        val element = state.infoset
        // this adjustment is so that automatic display of ".." doesn't fail
        // for the root element.
        val adjustedExpression =
          if ((element.parent eq null) && (expression == "..")) "."
          else expression
        val context = state.getContext()
        val namespaces = context.dpathCompileInfo.namespaces
        val expressionWithBraces =
          if (!DPathUtil.isExpression(adjustedExpression)) "{ " + adjustedExpression + " }"
          else adjustedExpression
        val isEvaluatedAbove = false
        try {
          val hostForDiags = new DebuggerHost(state.tunable)
          val compiledExpression = eCompilers.AnyRef.compileExpression(
            debuggerQName,
            NodeInfo.AnyType, expressionWithBraces, namespaces, context.dpathCompileInfo,
            isEvaluatedAbove, hostForDiags, context.dpathElementCompileInfo)
          val res = compiledExpression.evaluate(state)
          val warnings = hostForDiags.getDiagnostics.filterNot(_.isError)
          warnings.foreach {
            debugPrintln(_)
          }
          res match {
            case ie: InfosetElement => debugPrettyPrintXML(ie)
            case nodeSeq: Seq[Any] => nodeSeq.foreach { a =>
              a match {
                case ie: InfosetElement => debugPrettyPrintXML(ie)
                case _ => debugPrintln(a)
              }
            }
            case _ => debugPrintln(res)
          }
        } catch {
          case e: ErrorsNotYetRecorded => {
            val diags = e.diags
            val newDiags = diags.flatMap { d =>
              d match {
                case rel: RelativePathPastRootError => Nil
                case _ => List(d)
              }
            }
            if (!newDiags.isEmpty) {
              val ex = new ErrorsNotYetRecorded(newDiags)
              throw new DebugException("expression evaluation failed: %s".format(Misc.getSomeMessage(ex).get))
            }
          }
          case s: scala.util.control.ControlThrowable => throw s
          //
          // If we eval(.) on a node that has no value, we get a RSDE thrown.
          //
          // Users (such as tests in daffodil's cli module) can set up a 'display eval (.)' and then
          // single steps until they start parsing an element which has no value.
          // That will throw this RSDE. If we recognize this situation, we
          // display the empty element.
          //
          case r: RuntimeSchemaDefinitionError if r.getCause() ne null => r.getCause() match {
            case nd: InfosetNoDataExceptionBase => {
              //
              // Displays the empty element since it has no value.
              //
              debugPrettyPrintXML(nd.diElement)
              state.suppressDiagnosticAndSucceed(r)
            }
            case _ => throw r
          }
          case e: Throwable => {
            val ex = e // just so we can see it in the debugger.
            throw new DebugException("expression evaluation failed: %s".format(Misc.getSomeMessage(ex).get))
          }
        }
        DebugState.Pause
      }
    }
