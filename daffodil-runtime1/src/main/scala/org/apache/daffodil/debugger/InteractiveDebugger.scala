/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.debugger

import org.apache.daffodil.debugger.cmd.DebugCommandBase
import org.apache.daffodil.dpath.ExpressionEvaluationException
import org.apache.daffodil.dpath.NodeInfo
import org.apache.daffodil.dsom.ExpressionCompilerClass
import org.apache.daffodil.dsom.RuntimeSchemaDefinitionError
import org.apache.daffodil.exceptions.UnsuppressableException
import org.apache.daffodil.infoset._
import org.apache.daffodil.oolag.ErrorsNotYetRecorded
import org.apache.daffodil.processors._
import org.apache.daffodil.processors.parsers.ConvertTextCombinatorParser
import org.apache.daffodil.processors.parsers._
import org.apache.daffodil.processors.unparsers.UState
import org.apache.daffodil.processors.unparsers.UStateForSuspension
import org.apache.daffodil.processors.unparsers.Unparser
import org.apache.daffodil.util.Misc
import org.apache.daffodil.xml.QName

abstract class InteractiveDebuggerRunner {
  def init(id: InteractiveDebugger): Unit
  def getCommand: String
  def lineOutput(line: String): Unit
  def fini(): Unit
}

class InteractiveDebugger(runner: InteractiveDebuggerRunner, eCompilers: ExpressionCompilerClass) extends Debugger {
  var debugState: DebugState.Type = DebugState.Pause

  var previousProcessorState: StateForDebugger = _

  override def init(state: PState, parser: Parser): Unit = {
    runner.init(this)
    previousProcessorState = state.copyStateForDebugger
  }

  override def init(state: UState, unparser: Unparser): Unit = {
    runner.init(this)
    previousProcessorState = state.copyStateForDebugger
  }

  override def fini(parser: Parser): Unit = {
    runner.fini
  }

  override def fini(unparser: Unparser): Unit = {
    runner.fini
  }

  def debugStep(state: ParseOrUnparseState, processor: Processor, ignoreBreakpoints: Boolean): Unit = {
      // ignore debug steps called during suspensions, those must be handled differently
      if (state.isInstanceOf[UStateForSuspension]) {
        return
      }

      debugState = debugState match {
        case _ if ((state.processorStatus ne Success) && DebuggerConfig.breakOnFailure) => DebugState.Pause
        case DebugState.Continue | DebugState.Trace if !ignoreBreakpoints => {
          findBreakpoint(state, processor) match {
            case Some(bp) => {
              debugPrintln("breakpoint %s: %s   %s".format(bp.id, bp.breakpoint, bp.condition.getOrElse("")))
              DebugState.Pause
            }
            case None => debugState
          }
        }
        case DebugState.Step => DebugState.Pause
        case _ => debugState
      }

      if (debugState == DebugState.Pause || debugState == DebugState.Trace) {
        val dc = DebuggerConfig
        val rawDisplays = dc.displays
        val displays = rawDisplays.filter(_.enabled)
        displays.foreach { d =>
          runCommand(d.cmd, state, processor)
        }

        if (state.processorStatus ne Success) {
          debugPrintln("failure:")
          debugPrintln("%s".format(state.diagnostics.head.getMessage()), "  ")
        }

        if (debugState == DebugState.Trace) {
          debugPrintln("----------------------------------------------------------------- " + DebuggerConfig.parseStep)
        }
      }

      DebuggerConfig.parseStep += 1

      while (debugState == DebugState.Pause) {
        val args = readCmd
        debugState = runCommand(args, state, processor)
      }

      previousProcessorState = state.copyStateForDebugger
  }

  private def isInteresting(parser: Parser): Boolean = {
    val interesting = parser match {
      case _: ComplexTypeParser => false
      case _: SeqCompParser => false
      case _: RepeatingChildParser => false
      case _: ConvertTextCombinatorParser => false
      case _: CombinatorParser => false
      case _ => true
    }
    interesting
  }

  override def startElement(state: PState, parser: Parser): Unit = {
    debugStep(state, parser, false)
  }

  override def endElement(state: UState, unparser: Unparser): Unit = {
    debugStep(state, unparser, false)
  }

  override def before(before: PState, parser: Parser): Unit = {}

  override def after(state: PState, parser: Parser): Unit = {
    if (isInteresting(parser)) {
      debugStep(state, parser, DebuggerConfig.breakOnlyOnCreation)
    }
  }

  override def beforeRepetition(before: PState, processor: Parser): Unit = {}

  override def afterRepetition(after: PState, processor: Parser): Unit = {}

  private def isInteresting(unparser: Unparser): Boolean = {
    true
  }

  override def before(before: UState, unparser: Unparser): Unit = {
  }

  override def after(state: UState, unparser: Unparser): Unit = {
    if (isInteresting(unparser)) {
      debugStep(state, unparser, DebuggerConfig.breakOnlyOnCreation)
    }
  }

  private def readCmd(): Seq[String] = {
    val input = runner.getCommand.trim

    DebuggerConfig.history += input

    val cmd = input match {
      case "" => {
        DebuggerConfig.lastCommand
      }
      case _ => {
        DebuggerConfig.lastCommand = input
        input
      }
    }
    cmd.split(" ").filter(_ != "")
  }

  /**
   * Here the debugger depends on being able to evaluate expressions that might run into
   * problems like asking for data from elements that don't exist yet or have no values yet.
   *
   * There also can be compilation errors if the expressions aren't well formed or have type errors in them (such as
   * they don't return a boolean value).
   */
  private def evaluateBooleanExpression(expression: String, state: ParseOrUnparseState, processor: Processor): Boolean = {
    val context = state.getContext()
    try {
      //
      // compile the expression
      //
      val compiledExpr = try {
        val hostForDiags = new DebuggerHost(state.tunable)
        val ce = eCompilers.JBoolean.compileExpression(
          debuggerQName,
          NodeInfo.Boolean, expression, context.dpathCompileInfo.namespaces, context.dpathCompileInfo, false,
          hostForDiags, context.dpathCompileInfo)
        val warnings = hostForDiags.getDiagnostics.filterNot(_.isError)
        warnings.foreach {
          debugPrintln(_)
        }
        ce
      } catch {
        //
        // These are compile-time errors for the expression compilation
        //
        case errs: ErrorsNotYetRecorded => {
          debugPrintln(errs)
          throw errs
        }
      }
      //
      // evaluate the expression, and catch ways it can fail just because this is the debugger and it
      // isn't necessarily evaluating the expression in sensible places.
      //
      // Note also that the debugger does not use Evaluatable around the compiled expression. This is because
      // Evaluatable is really designed to be called from parsers/unparsers.
      //
      try {
        val res = compiledExpr.evaluate(state)
        res match {
          case b: java.lang.Boolean => b.booleanValue()
          case _ => false
        }
      } catch {
        case s: scala.util.control.ControlThrowable => throw s
        case u: UnsuppressableException => throw u
        case _: ExpressionEvaluationException | _: InfosetException | _: VariableException => {
          // ?? How do we discern for the user whether this is a problem with their expression or
          // the infoset is just not populated with the things the expression references yet?
          state.setSuccess()
          false
        }
        //
        // Most errors are coming back here as RSDE because that's what they get upconverted into.
        // Most expression problems are considered SDE.
        //
        case _: RuntimeSchemaDefinitionError => {
          state.setSuccess()
          false
        }
      }
    } catch {
      case s: scala.util.control.ControlThrowable => throw s
      case u: UnsuppressableException => throw u
      case e: Throwable => {
        println("caught throwable " + Misc.getNameFromClass(e) + ": " + Misc.getSomeMessage(e).get)
        state.setSuccess()
        false
      }
    }
  }

  private def findBreakpoint(state: ParseOrUnparseState, processor: Processor): Option[Breakpoint] = {
    val foundBreakpoint =
      DebuggerConfig.breakpoints
        .filter(_.enabled)
        .filter { bp =>

          //
          // Two syntaxes for breakpoints are accepted.
          // one is extended QNames e.g., foo, or pre:foo, or {uri}foo
          // the other is schema component paths like foo::bar::baz
          //
          val tryBPQName = QName.refQNameFromExtendedSyntax(bp.breakpoint)
          if (tryBPQName.isFailure) {
            //
            // Breakpoint specified by path syntax
            //
            bp.breakpoint == processor.context.path
          } else {
            //
            // must be the extended QName case.
            //
            processor.context match {
              case erd: ElementRuntimeData => {
                val elemQName = erd.namedQName
                val bpqnx = tryBPQName.get
                //
                // If the user provided the {uri}foo style syntax
                // we just need the namespace and name to match.
                //
                if (bpqnx.local == elemQName.local &&
                  bpqnx.namespace == elemQName.namespace) {
                  true
                } else {
                  //
                  // usage must have been just a QName e.g., foo:bar
                  // for the breakpoint, or mostlikely, just a local name bar.
                  //
                  val bpQNameString = bpqnx.toQNameString
                  val bpqn = processor.context.resolveQName(bpQNameString)
                  val isMatch = bpqn.toStepQName.matches(elemQName)
                  if (isMatch)
                    true
                  else {
                    //
                    // finally, if the bp was just specified as a local name
                    // then ok so long as the local name part matches.
                    //
                    // TODO: it would be good to know if this bp name is ambiguous
                    // In this case it will match ANY element having that local
                    // name. But if you want to be more selective of just the
                    // specific element in a specific namespace then you can use
                    // the extended QName syntax, or just a prefix on it.
                    val isLocalMatch = bpqnx.local == elemQName.local
                    isLocalMatch
                  }
                }
              }
              case _ => false
            }
          }
        }
        .find { bp =>
          bp.condition match {
            case Some(expression) => evaluateBooleanExpression(expression, state, processor)
            case None => true
          }
        }
    foundBreakpoint
  }

  private def runCommand(cmd: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
    try {
      DebugCommandBase(cmd, state, processor)
    } catch {
      case e: DebugException => {
        debugPrintln(e)
        DebugState.Pause
      }
    }
  }





/**********************************/
/**          Commands            **/
/**********************************/
  //DebugCommandBase.checkNameConflicts
}
