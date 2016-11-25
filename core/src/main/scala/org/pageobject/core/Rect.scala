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
package org.pageobject.core

import scala.language.implicitConversions

/**
 * A dimension containing the width and height of a screen element.
 */
case class Rect(x: Int, y: Int, width: Int, height: Int) {
  def apply(point: Point, dimension: Dimension): Rect = {
    Rect(point.x, point.y, dimension.width, dimension.height)
  }
}

object Rect {
  implicit def toPoint(rect: Rect): Point = Point(rect.x, rect.y)

  implicit def toDimension(rect: Rect): Dimension = Dimension(rect.width, rect.height)
}
