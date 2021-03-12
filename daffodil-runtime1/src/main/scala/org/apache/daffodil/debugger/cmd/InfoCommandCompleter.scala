package org.apache.daffodil.debugger.cmd



class InfoCommandCompleter(dc: DebugCommand) extends DebugCommandBase.DebugCommandCompleter(dc) {
        override def getCompleteString(args: String) = {
          val lastInfoCommand =
            if (args.endsWith(" ")) {
              "" // this allows the subcommand completers to match anything
            } else {
              // otherwise, it will only match against the last info argument,
              // so 'info foo bar inf\t' will match 'inf' to infoset. The
              // default getComplteString would match against 'foo bar inf',
              // which wouldn't find anything
              args.split("\\s+").last
            }
          lastInfoCommand
        }
      }
