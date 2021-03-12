package org.apache.daffodil.debugger.cmd

import jline.console.completer.AggregateCompleter
import jline.console.completer.Completer
import jline.console.completer.StringsCompleter
import org.apache.daffodil.debugger.DebugException
import org.apache.daffodil.debugger.DebugState
import org.apache.daffodil.debugger.debugPrintln
import org.apache.daffodil.exceptions.Assert
import org.apache.daffodil.processors.ParseOrUnparseState
import org.apache.daffodil.processors.Processor

import scala.collection.JavaConverters._

abstract class DebugCommand {
    val name: String
    lazy val short: String = name(0).toString
    val desc: String
    val longDesc: String
    val subcommands: Seq[DebugCommand] = Seq()
    val hidden = false

    def apply(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type = {
      validate(args)
      act(args, state, processor)
    }

    def validate(args: Seq[String]): Unit

    def act(args: Seq[String], state: ParseOrUnparseState, processor: Processor): DebugState.Type

    override def equals(that: Any): Boolean = {
      that match {
        case str: String => (str == name || str == short)
        case _ => super.equals(that)
      }
    }

    def help(args: Seq[String]): Unit = {

      // this wraps a line of text to a maximum width, breaking on space
      def wrapLine(line: String, width: Int): List[String] = {
        if (line.length == 0) {
          Nil
        } else {
          val spaceIndex = line.lastIndexOf(" ", width)
          if (line.length < width || spaceIndex == -1) {
            List(line)
          } else {
            val wrapped = line.take(spaceIndex) :: wrapLine(line.drop(spaceIndex + 1), width)
            wrapped
          }
        }
      }

      args.length match {
        case 0 => {
          val visibleSubcommands = subcommands.filter(!_.hidden)
          if (name != "") {
            debugPrintln("%s".format(longDesc))
            if (!visibleSubcommands.isEmpty) {
              debugPrintln()
              debugPrintln("Subcommands:")
            }
          }
          val maxLen = visibleSubcommands.foldLeft(0) { (i, c) => i.max(c.name.length) }
          val formatString = "  %-" + maxLen + "s  %s"
          visibleSubcommands.foreach(c => {
            val descColumnWidth = 75
            val descLines = wrapLine(c.desc, descColumnWidth - maxLen)
            val prefixes = c.name :: List.fill(descLines.length - 1)("")
            prefixes.zip(descLines).foreach { case (p, d) => debugPrintln(formatString.format(p, d)) }
          })
        }
        case _ => {
          val subcmd = args.head
          val subcmdArgs = args.tail
          subcommands.find(_ == subcmd) match {
            case Some(cmd) => cmd.help(subcmdArgs)
            case None => throw new DebugException("unknown command: %s".format(subcmd))
          }
        }
      }
    }

    class DebugCommandCompleter(dc: DebugCommand) extends Completer {
      val subcommandsCompleter = new AggregateCompleter(dc.subcommands.sortBy(_.name).map(_.completer): _*)

      def getCompleteString(args: String) = {
        // just remove leading whitespace
        val trimmed = args.replaceAll("^\\s+", "")
        trimmed
      }

      def complete(buffer: String, cursor: Int, candidates: java.util.List[CharSequence]): Int = {
        val cmds = buffer.replaceAll("^\\s+", "").split("(?= )", 2).toList
        val (cmd, args) = cmds match {
          case c :: rest => rest match {
            case a :: Nil => (c, a)
            case Nil => (c, "")
            case _ => Assert.impossible("cmd/args were split incorrectly")
          }
          case Nil => ("", "")
        }

        if (args != "") {
          if (dc == cmd) {
            val completeString = getCompleteString(args)
            val subcandidates = new java.util.ArrayList[CharSequence]
            val newCursor = subcommandsCompleter.complete(completeString, cursor, subcandidates)
            val seq = subcandidates.asScala
            seq.foreach(c => candidates.add(c))
            buffer.lastIndexOf(completeString) + newCursor
          } else {
            -1
          }
        } else {
          if (dc.name.startsWith(cmd)) {
            candidates.add(dc.name + " ")
            buffer.lastIndexOf(cmd)
          } else {
            -1
          }
        }
      }
    }

    def completer: Completer = {
      if (subcommands.isEmpty) {
        new StringsCompleter(name)
      } else {
        new DebugCommandCompleter(this)
      }
    }

    // This ensures that there are no naming conflicts (e.g. short form names
    // conflict). This is really just a sanity check, and really only needs to
    // be run whenever names change or new commands are added.

    // Uncomment this and the DebugCommandBase.checkNameConflicts line to do a
    // check when changes are made.
    //
    /*
    def checkNameConflicts() {
      val allNames = subcommands.map(_.name) ++ subcommands.filter{ sc => sc.name != sc.short }.map(_.short)
      val duplicates = allNames.groupBy{ n => n }.filter{ case(_, l) => l.size > 1 }.keys
      if (duplicates.size > 0) {
        Assert.invariantFailed("Duplicate debug commands found in '%s' command: ".format(name) + duplicates)
      }
      subcommands.foreach(_.checkNameConflicts)
    }
    */
  }
