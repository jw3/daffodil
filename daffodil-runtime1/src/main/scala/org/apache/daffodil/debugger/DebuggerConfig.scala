package org.apache.daffodil.debugger

import org.apache.daffodil.schema.annotation.props.gen.Representation

object DebuggerConfig {
    /* the max number of lines to tail the infoset, infosetLines <= 0 means print
     * everything */
    var infosetLines: Int = -1

    /* the number of parent elements to include when displaying the infoset. -1
     * means show all parents. 0 or more means show 0 or more parent elements. */
    var infosetParents: Int = -1

    /* the max number of bytes to display when displaying data */
    var dataLength: Int = 70

    /* the lenght at which to wrap output infoset/data/etc */
    var wrapLength: Int = 80

    /* whether or not to break only on element creation or anytime the element is seen */
    var breakOnlyOnCreation: Boolean = true

    /* whether or not to break on failure */
    var breakOnFailure: Boolean = false

    /* list of breakpoints */
    val breakpoints = collection.mutable.ListBuffer[Breakpoint]()
    var breakpointIndex: Int = 1

    /* list of displays */
    val displays = collection.mutable.ListBuffer[Display]()
    var displayIndex: Int = 1

    /* whether to remove hidden elements when displaying the infoset */
    var removeHidden: Boolean = false

    /* list of info commands to exclude when running 'info diff' */
    var diffExcludes: Seq[String] = Seq.empty

    /* stores the last actual command (i.e. not a "") that was executed */
    var lastCommand: String = ""

    /* stores the full list of commands as typed, even blanks. We need to
     * maintain our own because the jline history ignores empty commands and
     * duplicate commands. That can be configured, but jline's history is much
     * more useful with the history behaving that way. This is really only used
     * for the 'history' command. */
    val history = scala.collection.mutable.ListBuffer[String]()

    /* keeps track of which parse step we're on for trace output */
    var parseStep = 0

    /* how to display data */
    var representation: Representation.Value = Representation.Text
  }
