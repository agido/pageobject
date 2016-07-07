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

basedir=$(dirname $(readlink -f $0))
vncdir=$basedir/$(uname -s)-$(uname -m)

function mute() {
	$@ >/dev/zero 2>&1
}

function vncid() {
	echo $1 | cut -b 2-
}

function vncport() {
	echo $(($(vncid $1) + 8000))
}

function vncdisplay() {
	local id=$(vncid $1)
	echo $(((id / 100 * 300) + (id % 100)))
}

function lockfile() {
	echo /tmp/.X$(vncid $1)-lock
}

function islocked() {
	test -f $(lockfile $1)
}

function isfn() {
	mute type $1
}

function fatal() {
	local message="$1"
	local code="$2"
	echo $message
	if [ -z "$code" ]; then
		exit 1
	else
		exit $code
	fi
}

function require() {
	local what=$1
	local message=$2
	if [ -z "$what" ]; then
		fatal "$message"
	fi
}

function call() {
	local cmd=$1
	local message=$2
	shift || true
	shift || true
	if isfn "$cmd"; then
		$cmd $@
	else
		fatal "$message"
	fi
}

function isexec() {
	local cmd=$(which $1)
	if [ -z "$cmd" -o ! -e "$cmd" -o ! -x "$cmd" ] ; then
		fatal "$1 not found" 127
	fi
}

function stop() {
	local pid=$1
	local what=$2
	if mute kill $pid ; then
		echo "stopped $what"
	else
		echo "$what was not running"
	fi
}

function cmd_start() {
	local vncPid
	local terminalPid
	local viewerPid
	local waitCount=2

	local id=$1
	local display=$2
	shift || true
	shift || true
	shift || true

	local message="Syntax: $0 start <id> [display]"
	require "$id" "$message"
	require "$SELENIUM_COMMAND" "SELENIUM_COMMAND not set!"

	isexec $(echo $SELENIUM_COMMAND | cut -d ' ' -f 1)
	isexec $vncdir/Xvnc
	isexec xterm
	if [ -n "$CLIENT_DISPLAY_PORT" ]; then
		isexec $vncdir/vncviewer
	fi

	export WAIT_FOR_PID=$PPID
	export CLIENT_DISPLAY_PORT=$display
	export DISPLAY=:$(vncdisplay $id)

	islocked $DISPLAY && fatal "display $DISPLAY is already in use!"

	$vncdir/Xvnc $DISPLAY -fp "" -rfbport $(vncport $id) -nopn -geometry 1920x1080 PasswordFile=$basedir/.vncpasswd 2>&1 &
	vncPid=$!

	echo "waiting for vnc server"
	for((i=0;i<100;i++)); do
		islocked $DISPLAY && break
		mute kill -0 $vncPid || break
		sleep 0.1
	done
	islocked $DISPLAY || fatal "vnc server $DISPLAY could not be started!"
	echo "vnc server is ready!"

	xterm -maximized -fa 'Monospace' -fs 14 -e $SELENIUM_COMMAND &
	terminalPid=$!

	if [ -n "$CLIENT_DISPLAY_PORT" ]; then
		DISPLAY=$CLIENT_DISPLAY_PORT $vncdir/vncviewer ::$(vncport $DISPLAY) PasswordFile=$basedir/.vncpasswd &
		viewerPid=$!
		waitCount=3
	fi

	echo "waiting for pid $WAIT_FOR_PID"

	while [ "$(jobs | wc -l)" -eq $waitCount ]; do
		mute kill -0 $WAIT_FOR_PID || break
		sleep 0.5
		mute jobs
	done

	if [ -n "$CLIENT_DISPLAY_PORT" ]; then
		stop $viewerPid vncviewer
	fi
	stop $terminalPid xterm
	stop $vncPid vncserver
}

function cmd_check() {
	local id=$1
	local seleniumPort=$2
	shift || true
	shift || true
	local message="Syntax: $0 check <id> <seleniumPort>"
	require "$id" "$message"
	require "$seleniumPort" "$message"

	mute curl http://127.0.0.1:$seleniumPort/wd/hub/status || exit 1
}

function main() {
	local cmd=$1

	shift || true
	call "cmd_$cmd" "Syntax: $0 <start|check>" $@
}

main $@ 2>&1

exit 0
