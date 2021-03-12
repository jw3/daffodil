package org.apache.daffodil.debugger

import org.apache.daffodil.util.Misc

case class DebugException(str: String, cause: Throwable) extends java.lang.Exception(str, cause) {
    override def toString = "Debugger error: " + Misc.getSomeMessage(this).get
    def this(str: String) = this(str, null)
  }
