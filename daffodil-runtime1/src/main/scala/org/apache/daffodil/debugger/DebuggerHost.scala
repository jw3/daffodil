package org.apache.daffodil.debugger

import org.apache.daffodil.BasicComponent
import org.apache.daffodil.api.DaffodilTunables
import org.apache.daffodil.oolag.OOLAG.OOLAGHostImpl

/**
 * A stub OOLAGHost is needed to accumulate warnings that may be created
 * during expression compilation in the debugger.
 */
class DebuggerHost(override val tunable: DaffodilTunables)
  extends OOLAGHostImpl(null) // null means this is the root OOLAG Host
  with BasicComponent {

  /**
   * As seen from class DebuggerHost, the missing signatures are as follows.
   *  *  For convenience, these are usable as stub implementations.
   */
  // Members declared in org.apache.daffodil.xml.ResolvesQNames
  def namespaces: scala.xml.NamespaceBinding = ???
  def unqualifiedPathStepPolicy: org.apache.daffodil.api.UnqualifiedPathStepPolicy = ???
  // Members declared in org.apache.daffodil.exceptions.ThrowsSDE
  def schemaFileLocation: org.apache.daffodil.exceptions.SchemaFileLocation = ???
}
