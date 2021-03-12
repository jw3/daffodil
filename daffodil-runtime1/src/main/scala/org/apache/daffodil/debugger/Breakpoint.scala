package org.apache.daffodil.debugger

case class Breakpoint(id: Int, breakpoint: String) extends Disablable {
  var condition: Option[String] = None
}
