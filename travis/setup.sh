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

if [ "$(uname)" = "Linux" ]; then
	ARCH=linux
elif [ "$(uname)" = "Darwin" ]; then
	ARCH=osx
	alias find='gfind'
	alias sed='gsed'
	alias awk='gawk'
else
	echo "Unsupported OS '$(uname)'"
	exit 1
fi

function _md5sum() {
	local file=$1
	if [ $ARCH = "linux" ]; then
		md5sum $file 2>/dev/zero | cut -d ' ' -f 1
	else
		md5 $file 2>/dev/zero | cut -d '=' -f 2 | cut -b 2-
	fi
}

function download() {
	local file=$1
	local url=$2
	local sum=$3

	if [ "$(_md5sum $file)" != "$sum" ]; then
		rm -f $file $file.tmp || true
		echo "Downloading $file"
		if curl -sS -L $url -o $file.tmp ; then
			mv $file.tmp $file
		else
			exit 1
		fi
	fi

	if [ "$(_md5sum $file)" != "$sum" ]; then
		echo "Downloading $file failed!"
		exit 1
	fi
}

function unpack() {
	local from=$1
	local what=$2
	local file=$3
	local sum=$4

	if [ "$(_md5sum $file)" != "$sum" ]; then
		if [[ "$from" == *.zip ]]; then
			unzip $from $what -d $(dirname $file)
		else
			rm -f $file || true
			tar xf $from --transform "s:$what:$file:" $what
		fi
	fi
	if [ "$(_md5sum $file)" != "$sum" ]; then
		rm -f $file || true
		echo "unpacking $file failed!"
		exit 1
	fi
}

function setup_vnc() {
	if [ $ARCH = "linux" ]; then
		local tigervnc=tigervnc-Linux-x86_64-1.6.0.tar.gz
		download vnc/$tigervnc https://bintray.com/tigervnc/stable/download_file?file_path=$tigervnc 7379a5b507db2a490fbe05b4c9447efb
		unpack vnc/$tigervnc ./usr/bin/Xvnc vnc/Xvnc 500f4926f0967086d3360cd45b9f6b8c
		unpack vnc/$tigervnc ./usr/bin/vncviewer vnc/vncviewer 091e8e64094da4993b75a2838475187d
	else
		echo "skipping.. vnc currently not tested on OSX!"
	fi
}

function setup_seleniumserver() {
	download selenium/selenium-server-standalone.jar https://selenium-release.storage.googleapis.com/2.53/selenium-server-standalone-2.53.1.jar 63a0b96eab18f8420b9bba2f0f5d380c
}

function setup_chromedriver() {
	if [ $ARCH = "linux" ]; then
		download selenium/chromedriver_linux64.zip https://chromedriver.storage.googleapis.com/2.22/chromedriver_linux64.zip 2a5e6ccbceb9f498788dc257334dfaa3
		unpack selenium/chromedriver_linux64.zip chromedriver selenium/chromedriver e74acdb8d4723f4d330f3ff5d846d204
	else
		echo "skipping.. chromedriver currently not tested on OSX!"
	fi
}

function setup() {
	setup_vnc
	setup_seleniumserver
	setup_chromedriver
}

setup
