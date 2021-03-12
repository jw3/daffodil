package org.apache.daffodil.debugger

import org.apache.daffodil.processors.StateForDebugger

trait InfoDiffable {
        /**
        * Outputs any differences between previousProcessorState and state for the mixed in debugger command
        *
        * Differences should be displayed via the debugPrintln command. Output
        * should include the command name and two space indentation (e.g. pass
        * in "  " as the second argument of debugPrintln).
        *
        * @return true if any differences were found and output, false otherwise
        */
        def diff(pre: StateForDebugger, post: StateForDebugger): Boolean
      }
