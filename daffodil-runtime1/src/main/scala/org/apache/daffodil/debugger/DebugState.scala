package org.apache.daffodil.debugger

import org.apache.daffodil.util.Enum

object DebugState extends Enum {
    sealed abstract trait Type extends EnumValueType

    case object Continue extends Type

    case object Step extends Type

    case object Pause extends Type

    case object Trace extends Type
}
