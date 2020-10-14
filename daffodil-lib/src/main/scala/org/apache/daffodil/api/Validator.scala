/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.api

import org.xml.sax.ErrorHandler

trait Validator {
  def name(): String = getClass.getTypeName.toLowerCase
  def validateXML(document: java.io.InputStream, errHandler: ErrorHandler, args: Validator.Arguments): Unit
}

object Validator {
  type Arguments = Seq[Validator.Argument]

  case class Argument(key: String, value: String)
  object Argument {
    val DefaultKey = "default"
    def apply(value: String): Argument = Argument(DefaultKey, value)
  }

  object CompilerOps {
    trait CheckArgs {
      def checkArgs(args: Validator.Arguments): Validator.Arguments
    }
  }
}

case class ValidatorNotFoundException(name: String) extends Exception(s"$name validator was not found")
case class ValidatorCompilationException(message: String) extends Exception(message)

abstract class CompiledValidator(val v: Validator) {
  final def name: String = v.name()
  def validateXML(document: java.io.InputStream, errHandler: ErrorHandler): Unit
}
