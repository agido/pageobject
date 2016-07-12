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

set -e

function decrypt() {
	openssl aes-256-cbc -pass env:ENCRYPTION_PASSWORD -in $1.bin -out $1 -d
}

if [ -z "$ENCRYPTION_PASSWORD" ]; then
	echo 'missing $ENCRYPTION_PASSWORD'
	exit 1
fi

decrypt .gnupg/pubring.gpg
decrypt .gnupg/secring.gpg
decrypt .gnupg/credentials.sbt

mv .gnupg/credentials.sbt project

sbt core/publishSigned scalatest/publishSigned
