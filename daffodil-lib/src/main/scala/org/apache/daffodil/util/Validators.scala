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

import java.util.ServiceLoader

import org.apache.daffodil.api.Validator

object Validators {
  import scala.collection.JavaConverters._

  private val impls = new ThreadLocal[Map[String, Validator]] {
    override def initialValue = {
        ServiceLoader
          .load(classOf[Validator])
          .iterator()
          .asScala
          .map(v => v.name() -> v)
          .toMap
    }
  }

  def all(): Map[String, Validator] = impls.get()
  def find(name: String): Option[Validator] = all().get(name)
  def exists(name: String): Boolean = all().exists(name == _._1)
  val default: Validator = new DefaultValidatorSPIProvider
  def checkArgs(name: String, args: Validator.Arguments): Either[String, Unit] =
    find(name) match {
      case None => Left(s"Validator '$name' was not found.")
      case Some(v) => v.checkArgs(args)
    }
}
