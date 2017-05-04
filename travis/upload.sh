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

function decrypt() {
	openssl aes-256-cbc -pass env:ENCRYPTION_PASSWORD -in $1.bin -out $1 -d
}

if [ "$PUBLISH" != "true" ]; then
	exit 0
fi

if [[ `cut -f2 -d\" version.sbt` == *-SNAPSHOT ]]; then
    if [ "$TRAVIS_BRANCH" != "develop" ]; then
	    exit 0
    fi
else
    if [ "$TRAVIS_BRANCH" != "master" ]; then
	    exit 0
    fi
fi

if [ -z "$ENCRYPTION_PASSWORD" ]; then
	echo 'Warning: missing ENCRYPTION_PASSWORD'
else
	decrypt .gnupg/pubring.gpg
	decrypt .gnupg/secring.gpg
	decrypt .gnupg/credentials.sbt

	mkdir -p ~/.sbt/0.13/
	mv .gnupg/credentials.sbt ~/.sbt/0.13/

	sbt core/aether-deploy scalatest/aether-deploy
fi

if [ -z "$UPDATEIMPACT_API_KEY" ]; then
	echo 'Warning: missing UPDATEIMPACT_API_KEY'
else
	sbt updateImpactSubmit
fi

if [ -z "$SCOVER" ]; then
	# skip
	true
elif [ -z "$COVERALLS_REPO_TOKEN" ]; then
	echo 'Warning: missing COVERALLS_REPO_TOKEN'
else
	sbt coveralls
fi
