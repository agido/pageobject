/*
 * Copyright 2016 agido GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageobject.core.tools

/**
 * In opposite to scala's DynamicVariable this class is using a ThreadLocal.
 */
class DynamicOptionVariable[T](default: Option[T] = None) {
  def this(defaultValue: T) = {
    this(Some(defaultValue))
  }

  private val threadLocal = new ThreadLocal[Option[T]] {
    override def initialValue = default
  }

  def option: Option[T] = threadLocal.get

  def value: T = option.get

  def withValue[S](newval: T)(thunk: => S): S = withValue(Some(newval))(thunk)

  def withValue[S](newval: Option[T])(thunk: => S): S = {
    val oldval = option
    threadLocal.set(newval)
    try thunk
    finally threadLocal.set(oldval)
  }
}
