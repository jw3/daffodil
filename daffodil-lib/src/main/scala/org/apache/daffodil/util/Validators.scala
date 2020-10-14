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

package org.apache.daffodil.util

import java.io.InputStream
import java.util.ServiceLoader

import org.apache.daffodil.api.CompiledValidator
import org.apache.daffodil.api.Validator
import org.apache.daffodil.api.Validator.CompilerOps.CheckArgs
import org.apache.daffodil.api.ValidatorNotFoundException
import org.xml.sax.ErrorHandler

import scala.collection.JavaConverters._

object Validators {
  val default: Validator = new DefaultValidatorSPIProvider
  def compiler(): ValidatorCompiler = new Compiler

  trait ValidatorCompiler {
    def find(name: String): Option[CompiledValidator]
    def isRegistered(name: String): Boolean
    def compile(name: String, args: Validator.Arguments): CompiledValidator

  }
  private class Compiler extends ValidatorCompiler {
    private var compiled = Map.empty[String, CompiledValidator]
    private lazy val impls: Map[String, Validator] =
      ServiceLoader
        .load(classOf[Validator])
        .iterator()
        .asScala
        .map(v => v.name() -> v)
        .toMap

    private def impl(name: String): Option[Validator] = impls.get(name)
    def find(name: String): Option[CompiledValidator] = compiled.get(name)
    def isRegistered(name: String): Boolean = impls.contains(name)

    def compile(name: String, args: Validator.Arguments): CompiledValidator = {
      find(name) match {
        case Some(cv) => cv
        case None =>
          val vv = impl(name).getOrElse(throw ValidatorNotFoundException(name))
          val aa = vv match {
            case ac: CheckArgs => ac.checkArgs(args)
            case _             => args
          }
          val cv = new CompiledValidator(vv) {
            def validateXML(document: InputStream,
                            errHandler: ErrorHandler): Unit = {
              v.validateXML(document, errHandler, aa)
            }
          }
          addcv(name, cv)
          cv
      }
    }

    private def addcv(name: String, cv: CompiledValidator): Unit =
      compiled = compiled + (name -> cv)
  }
}
