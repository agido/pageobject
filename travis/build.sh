#!/bin/bash
#
# Copyright 2016 agido GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#	 http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

export RUN_WITH_DRIVERS=org.pageobject.core.driver.vnc.DefaultVncDriverFactoryList
export FIREFOX_LIMIT=0

if [[ "$TRAVIS_SCALA_VERSION" == 2.10* ]]; then
    # fix for scala 2.10.x only
    export SBT_OPTS="-Dscalac.patmat.analysisBudget=off"
fi

sbt scalastyle

if [ -z "$SCOVER" ]; then
	sbt test
else
	sbt coverage test coverageReport
	sbt coverageAggregate
fi
