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
package org.pageobject.core.dsl

/**
 * This trait is a summary of all PageObject DSL traits.
 *
 * They are two groups of PageObject DSL traits:
 *
 * <b>BrowserPageDsl<b>
 * <li>implements all commands that should be used by PageObjects</li>
 * <li>should not interact with the browser window (like navigating)</li>
 *
 * <b>BrowserControlDSL<b>
 * <li>implements commands provided by the old acala test selenium dsl</li>
 * <li>should normally be not needed</li>
 * <li>PageBrowser</li>
 *
 */
trait BrowserDsl extends BrowserPageDsl with BrowserControlDsl
