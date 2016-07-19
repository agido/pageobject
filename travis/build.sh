#!/bin/bash
#
# Copyright 2016 agido GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

export RUN_WITH_DRIVERS=org.pageobject.core.driver.vnc.DefaultVncDriverFactoryList
export FIREFOX_LIMIT=0

sbtCommands="test"

if [ -n "$COVERALLS_REPO_TOKEN" ]; then
	sbtCommands="coverage $sbtCommands coverageReport"
fi

sbt $@ $sbtCommands

if [ -n "$COVERALLS_REPO_TOKEN" ]; then
	sbt $@ coverageAggregate
fi
