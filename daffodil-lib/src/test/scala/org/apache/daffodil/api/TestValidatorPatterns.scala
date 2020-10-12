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

import org.junit.Test
import org.apache.daffodil.exceptions.Assert.invariantFailed
import org.junit.Assert.assertEquals

class TestValidatorPatterns {
  @Test def testNoArgsPattern(): Unit = {
    val vname = "vvv"
    s"$vname" match {
      case Validator.MultiArgsPattern(_, _) =>
        invariantFailed("should not have matched")
      case Validator.DefaultArgPattern(_, _) =>
        invariantFailed("should not have matched")
      case Validator.NoArgsPattern(v) =>
        assertEquals(vname, v)
      case _ =>
        invariantFailed("did not match")
    }
  }

  @Test def testMultiArgsPattern_Single(): Unit = {
    val vname = "vvv"
    val vargs = "foo=bar"
    s"$vname=$vargs" match {
      case Validator.MultiArgsPattern(v, args) =>
        assertEquals(vname, v)
        assertEquals(vargs, args)
      case _ =>
        invariantFailed("did not match")
    }
  }

  @Test def testMultiArgsPattern_IsNotDefault(): Unit = {
    val arg = s"vvv=some_default_argument_string"
    arg match {
      case Validator.MultiArgsPattern(_, args) =>
        invariantFailed(s"should not have matched on $arg")
      case _ =>
    }
  }

  @Test def testMultiArgsPattern_Multiple(): Unit = {
    val vname = "vvv"
    val vargs = "foo=bar,baz=fiz"
    s"$vname=$vargs" match {
      case Validator.MultiArgsPattern(v, args) =>
        assertEquals(vname, v)
        assertEquals(vargs, args)
      case _ =>
        invariantFailed("did not match")
    }

    val vargs2 = s"${vargs},foo2=bar2,baz2=fiz2"
    s"$vname=$vargs2" match {
      case Validator.MultiArgsPattern(v, args) =>
        assertEquals(vname, v)
        assertEquals(vargs2, args)
      case _ =>
        invariantFailed("did not match")
    }
  }
  @Test def testDefaultArgsPattern(): Unit = {
    val vname = "vvv"
    val varg = "some_default_argument_string"
    s"$vname=$varg" match {
      case Validator.MultiArgsPattern(_, _) =>
        invariantFailed("should not have matched")
      case Validator.DefaultArgPattern(v, arg) =>
        assertEquals(vname, v)
        assertEquals(varg, arg)
      case Validator.NoArgsPattern(_) =>
        invariantFailed("should not have matched")
      case _ =>
        invariantFailed("did not match")
    }
  }
}
