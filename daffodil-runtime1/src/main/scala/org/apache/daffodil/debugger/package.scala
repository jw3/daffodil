package org.apache.daffodil

import org.apache.daffodil.infoset.DIElement
import org.apache.daffodil.infoset.InfosetElement
import org.apache.daffodil.infoset.InfosetWalker
import org.apache.daffodil.infoset.XMLTextInfosetOutputter
import org.apache.daffodil.xml.GlobalQName
import org.apache.daffodil.xml.XMLUtils

package object debugger {
  val debuggerQName = GlobalQName(Some("daf"), "debugger", XMLUtils.dafintURI)

    def debugPrintln(obj: Any = "", prefix: String = ""): Unit = {
    obj.toString.split("\n").foreach { line =>
    {
      val out = "%s%s".format(prefix, line)
      runner.lineOutput(out)
    }
    }
  }

  def debugPrettyPrintXML(ie: InfosetElement): Unit = {
    val infosetString = infosetToString(ie)
    debugPrintln(infosetString)
  }

  def infosetToString(ie: InfosetElement): String = {
    val bos = new java.io.ByteArrayOutputStream()
    val xml = new XMLTextInfosetOutputter(bos, true)
    val iw = InfosetWalker(
      ie.asInstanceOf[DIElement],
      xml,
      walkHidden = !DebuggerConfig.removeHidden,
      ignoreBlocks = true,
      releaseUnneededInfoset = false)
    iw.walk(lastWalk = true)
    bos.toString("UTF-8")
  }
}
