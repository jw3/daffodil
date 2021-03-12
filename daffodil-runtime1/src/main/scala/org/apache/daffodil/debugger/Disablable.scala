package org.apache.daffodil.debugger

trait Disablable {
  var enabled = true
  def disable = { enabled = false }
  def enable = { enabled = true }
}
